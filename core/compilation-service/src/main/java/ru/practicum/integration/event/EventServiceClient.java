package ru.practicum.integration.event;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.integration.event.config.EventServiceClientConfig;
import ru.practicum.integration.event.dto.EventIdBatchRequest;
import ru.practicum.integration.event.dto.EventSummaryDto;
import ru.practicum.integration.event.fallback.EventServiceClientFallbackFactory;

@FeignClient(
    name = "event-service",
    path = "/internal/events",
    configuration = EventServiceClientConfig.class,
    fallbackFactory = EventServiceClientFallbackFactory.class
)

public interface EventServiceClient {
    @PostMapping("/by-ids")
    List<EventSummaryDto> getAllEventsById(@RequestBody EventIdBatchRequest request);
}
