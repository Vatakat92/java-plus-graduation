package ru.practicum.integration.stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.integration.stats.dto.EndpointHitDto;
import ru.practicum.integration.stats.dto.ViewStatsDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsServiceGateway {

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String START = "2000-01-01 00:00:00";

    private final StatsServiceClient statsServiceClient;

    @Value("${spring.application.name}")
    private String appName;

    public void hit(String uri, String ip) {
        try {
            EndpointHitDto payload = EndpointHitDto.builder()
                    .app(appName)
                    .uri(uri)
                    .ip(ip)
                    .timestamp(LocalDateTime.now().format(F))
                    .build();

            statsServiceClient.createHit(payload);
            log.debug("Stats hit sent: {} {}", ip, uri);
        } catch (Exception e) {
            log.warn("Failed to send stats hit for uri={} ip={}: {}", uri, ip, e.getMessage());
        }
    }

    public long getViewForEventId(Long eventId) {
        return getViewsForEventIds(List.of(eventId)).getOrDefault(eventId, 0L);
    }

    public Map<Long, Long> getViewsForEventIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Map.of();
        }

        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .toList();

        try {
            List<ViewStatsDto> stats = statsServiceClient.getStats(
                    START,
                    LocalDateTime.now().format(F),
                    uris,
                    true
            );

            Map<Long, Long> result = new HashMap<>();
            eventIds.forEach(id -> result.put(id, 0L));

            if (stats == null || stats.isEmpty()) {
                return result;
            }

            for (ViewStatsDto stat : stats) {
                Long eventId = extractEventId(stat.getUri());
                if (eventId != null) {
                    result.put(eventId, stat.getHits());
                }
            }

            return result;
        } catch (Exception e) {
            log.warn("Failed to fetch stats for eventIds={}: {}", eventIds, e.getMessage());
            return eventIds.stream().collect(
                    java.util.stream.Collectors.toMap(id -> id, id -> 0L)
            );
        }
    }

    private Long extractEventId(String uri) {
        if (uri == null || !uri.startsWith("/events/")) {
            return null;
        }

        try {
            return Long.parseLong(uri.substring("/events/".length()));
        } catch (NumberFormatException e) {
            log.warn("Unable to extract eventId from uri={}", uri);
            return null;
        }
    }
}