package ru.practicum.integration.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exceptions.ServiceUnavailableException;
import ru.practicum.integration.event.exceptions.EventServiceUnavailableException;

@Component
@RequiredArgsConstructor
public class EventServiceGateway {

    private final EventServiceClient eventClient;

    public Boolean existsByCategoryId(Long categoryId) {
        try {
            return eventClient.existsByCategoryId(categoryId);
        } catch (EventServiceUnavailableException e) {
            throw new ServiceUnavailableException("Event service is temporarily unavailable");
        }
    }
}