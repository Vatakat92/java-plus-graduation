package ru.practicum.integration.stats;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.integration.stats.config.StatsServiceClientConfig;
import ru.practicum.integration.stats.dto.EndpointHitDto;
import ru.practicum.integration.stats.dto.ViewStatsDto;
import ru.practicum.integration.stats.fallback.StatsServiceClientFallbackFactory;

@FeignClient(
    name = "stats-server",
    configuration = StatsServiceClientConfig.class,
    fallbackFactory = StatsServiceClientFallbackFactory.class
)

public interface StatsServiceClient {
    @PostMapping(value = "/hit", consumes = APPLICATION_JSON_VALUE)
    EndpointHitDto createHit(@RequestBody EndpointHitDto hitDto);

    @GetMapping("/stats")
    List<ViewStatsDto> getStats(
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam("unique") boolean unique
    );
}
