package ru.practicum.integration.request.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.integration.request.RequestServiceClient;
import ru.practicum.integration.request.exceptions.RequestServiceUnavailableException;

@Component
@Slf4j
public class RequestServiceClientFallbackFactory implements FallbackFactory<RequestServiceClient> {

    @Override
    public RequestServiceClient create(Throwable cause) {
        return (userId, eventId) -> {
            log.warn("Request service is unavailable. userId={}, eventId={}, cause={}", userId, eventId, cause.toString());
            throw new RequestServiceUnavailableException("Request service is unavailable.", cause);
        };
    }
}