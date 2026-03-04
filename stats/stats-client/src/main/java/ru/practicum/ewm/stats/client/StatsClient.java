package ru.practicum.ewm.stats.client;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.client.props.ClientProperties;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class StatsClient {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate rest;
    private final ClientProperties props;
    private final String appName;
    private final DiscoveryClient discoveryClient;
    private final RetryTemplate retryTemplate;
    private final String statsServiceId;

    public StatsClient(@NonNull RestTemplateBuilder builder,
                       @NonNull ClientProperties props,
                       @Value("${spring.application.name:ewm-service}") String appName,
                       DiscoveryClient discoveryClient,
                       RetryTemplate retryTemplate,
                       @Value("${stats.service-id:stats-service}") String statsServiceId) {
        this.props = props;
        this.appName = appName;
        this.discoveryClient = discoveryClient;
        this.retryTemplate = retryTemplate;
        this.statsServiceId = statsServiceId;
        this.rest = builder
                .setConnectTimeout(props.getConnectTimeout())
                .setReadTimeout(props.getReadTimeout())
                .build();
        log.debug("StatsClient initialized: connectTimeout={}, readTimeout={}, hitMaxAttempts={}, hitBackoff={}ms, app={}, statsServiceId={}",
                props.getConnectTimeout(), props.getReadTimeout(),
                props.getHitMaxAttempts(), props.getHitBackoffMillis(), appName, statsServiceId);
    }

    public void hit(@NonNull String uri, @NonNull String ip) {
        EndpointHitDto dto = new EndpointHitDto();
        dto.setApp(appName);
        dto.setUri(uri.trim());
        dto.setIp(ip);
        dto.setTimestamp(LocalDateTime.now());
        hit(dto);
    }

    public void hit(@NonNull EndpointHitDto dto) {
        int attempt = 1;
        final int max = props.getHitMaxAttempts();
        final long baseBackoff = props.getHitBackoffMillis();
        final long cap = props.getHitBackoffCapMillis();

        while (true) {
            try {
                URI uri = makeUri("/hit");
                rest.postForEntity(uri, dto, EndpointHitDto.class);
                log.info("Hit sent successfully: id={}, uri={}", dto.getId(), dto.getUri());
                if (log.isTraceEnabled()) {
                    log.trace("POST /hit sent: app={}, uri={}, ip={}, ts={}",
                            dto.getApp(), dto.getUri(), dto.getIp(), dto.getTimestamp());
                }
                return;
            } catch (RestClientException ex) {
                if (attempt >= max) {
                    log.warn("StatsClient: POST /hit failed after {} attempt(s). Continue without stats. reason={}",
                            attempt, ex.toString(), ex);
                    return;
                }
                long delay = baseBackoff * (1L << (attempt - 1));
                if (delay > cap) delay = cap;
                log.info("StatsClient: POST /hit failed on attempt {}/{}. Retry in {} ms. reason={}",
                        attempt, max, delay, ex.toString());
                safeSleep(delay);
                attempt++;
            } catch (RuntimeException ex) {
                log.info("StatsClient: unexpected error on POST /hit. Continue without stats. {}", ex.toString(), ex);
                return;
            }
        }
    }

    public long viewsForEvent(@NonNull Long eventId) {
        return viewsForUri("/events/" + eventId, true);
    }

    public long viewsForUri(@NonNull String uri, boolean unique) {
        return viewsForUris(List.of(uri), unique).getOrDefault(uri.trim(), 0L);
    }

    public Map<String, Long> viewsForUris(@NonNull List<String> uris, boolean unique) {
        if (uris.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> normalized = uris.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();

        if (normalized.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            List<ViewStatsDto> list = stats(
                    LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                    LocalDateTime.now(),
                    normalized,
                    unique
            );

            Map<String, Long> hits = list.stream()
                    .collect(Collectors.groupingBy(
                            ViewStatsDto::getUri,
                            Collectors.summingLong(ViewStatsDto::getHits)
                    ));

            Map<String, Long> result = new HashMap<>();
            for (String uri : normalized) {
                result.put(uri, hits.getOrDefault(uri, 0L));
            }
            return result;
        } catch (Exception e) {
            log.warn("Stats unavailable for uris {}: {}", normalized, e.toString());
            return normalized.stream().collect(Collectors.toMap(u -> u, u -> 0L));
        }
    }

    public List<ViewStatsDto> stats(@NonNull LocalDateTime start,
                                    @NonNull LocalDateTime end,
                                    List<String> uris,
                                    boolean unique) {
        StringBuilder qs = new StringBuilder()
                .append("/stats")
                .append("?start=").append(fmt(start))
                .append("&end=").append(fmt(end))
                .append("&unique=").append(unique);

        if (uris != null && !uris.isEmpty()) {
            for (String u : uris) {
                qs.append("&uris=").append(u.trim());
            }
        }

        try {
            URI uri = makeUri(qs.toString());
            ResponseEntity<ViewStatsDto[]> resp = rest.getForEntity(uri, ViewStatsDto[].class);
            ViewStatsDto[] body = resp.getBody();
            return (body == null) ? Collections.emptyList() : Arrays.asList(body);
        } catch (Exception e) {
            log.warn("Failed to get stats: {}", e.toString());
            return Collections.emptyList();
        }
    }

    private URI makeUri(String path) {
        ServiceInstance instance = retryTemplate.execute(cxt -> getInstance());
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
    }

    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances(statsServiceId)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No instances found for service: " + statsServiceId
                    ));
        } catch (Exception exception) {
            throw new RuntimeException(
                    "Error discovering address of stats service with id: " + statsServiceId,
                    exception
            );
        }
    }

    private void safeSleep(long millis) {
        try {
            if (millis > 0) Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("StatsClient: retry sleep interrupted; giving up retries.");
        }
    }

    private static String fmt(LocalDateTime dt) {
        return FMT.format(dt).replace(' ', '+');
    }
}
