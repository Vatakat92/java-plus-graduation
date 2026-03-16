package ru.practicum.integration.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.integration.event.config.EventServiceClientConfig;
import ru.practicum.integration.event.fallback.EventServiceClientFallbackFactory;

@FeignClient(
    name = "event-service",
    path = "/internal/events",
    configuration = EventServiceClientConfig.class,
    fallbackFactory = EventServiceClientFallbackFactory.class
)

public interface EventServiceClient {
    @GetMapping("/by-category-id/{categoryId}")
    Boolean existsByCategoryId(@PathVariable Long categoryId);
}
