package ru.practicum.integration.stats.fallback;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.integration.stats.StatsServiceClient;
import ru.practicum.integration.stats.dto.EndpointHitDto;
import ru.practicum.integration.stats.dto.ViewStatsDto;
import ru.practicum.integration.stats.exceptions.StatsServiceUnavailableException;

@Component
@Slf4j
public class StatsServiceClientFallbackFactory implements FallbackFactory<StatsServiceClient> {

    @Override
    public StatsServiceClient create(Throwable cause) {
        return new StatsServiceClient() {
            @Override
            public EndpointHitDto createHit(EndpointHitDto hitDto) {
                log.warn("Stats service is unavailable while sending hit: uri={}, cause={}",
                        hitDto.getUri(), cause.toString());
                throw new StatsServiceUnavailableException("Stats service is unavailable.", cause);
            }

            @Override
            public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
                log.warn("Stats service is unavailable while fetching stats: uris={}, unique={}, cause={}",
                        uris, unique, cause.toString());
                throw new StatsServiceUnavailableException("Stats service is unavailable.", cause);
            }
        };
    }
}