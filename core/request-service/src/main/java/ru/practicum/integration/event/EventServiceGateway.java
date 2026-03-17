package ru.practicum.integration.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ServiceUnavailableException;
import ru.practicum.integration.event.dto.EventSummaryDto;
import ru.practicum.integration.event.exceptions.EventNotFoundException;
import ru.practicum.integration.event.exceptions.EventServiceUnavailableException;

@Component
@RequiredArgsConstructor
public class EventServiceGateway {

    private final EventServiceClient eventClient;

    public EventSummaryDto getEventById(Long eventId) {
        try {
            return eventClient.getEventById(eventId);
        } catch (EventNotFoundException e) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        } catch (EventServiceUnavailableException e) {
            throw new ServiceUnavailableException("Event service is temporarily unavailable");
        }
    }
}