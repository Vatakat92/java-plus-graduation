package ru.practicum.integration.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.integration.request.config.RequestServiceClientConfig;
import ru.practicum.integration.request.dto.RequestShortDto;
import ru.practicum.integration.request.fallback.RequestServiceClientFallbackFactory;

@FeignClient(
    name = "request-service",
    path = "/internal/requests",
    configuration = RequestServiceClientConfig.class,
    fallbackFactory = RequestServiceClientFallbackFactory.class
)

public interface RequestServiceClient {
    @GetMapping("/userid/{userId}/eventid/{eventId}")
    RequestShortDto getByRequesterById(@PathVariable("userId") Long userId,
                                       @PathVariable("eventId") Long eventId
    );
}
