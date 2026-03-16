package ru.practicum.integration.event.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.integration.event.EventServiceClient;
import ru.practicum.integration.event.exceptions.EventServiceUnavailableException;

@Component
@Slf4j
public class EventServiceClientFallbackFactory implements FallbackFactory<EventServiceClient> {

    @Override
    public EventServiceClient create(Throwable cause) {
        return categoryId -> {
            log.warn("Event service is unavailable. categoryId={}, cause={}", categoryId, cause.toString());
            throw new EventServiceUnavailableException("Event service is unavailable.", cause);
        };
    }
}