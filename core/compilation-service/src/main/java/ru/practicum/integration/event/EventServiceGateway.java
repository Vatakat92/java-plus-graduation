package ru.practicum.integration.event;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ServiceUnavailableException;
import ru.practicum.integration.event.dto.EventIdBatchRequest;
import ru.practicum.integration.event.dto.EventSummaryDto;
import ru.practicum.integration.event.exceptions.EventNotFoundException;
import ru.practicum.integration.event.exceptions.EventServiceUnavailableException;

@Component
@RequiredArgsConstructor
public class EventServiceGateway {

    private final EventServiceClient eventClient;

    public List<EventSummaryDto> getAllEventsById(List<Long> eventIds) {

        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }

        try {
            return eventClient.getAllEventsById(new EventIdBatchRequest(eventIds));
        } catch (EventNotFoundException e) {
            throw new NotFoundException("Events with ids=" + eventIds + " was not found");
        } catch (EventServiceUnavailableException e) {
            throw new ServiceUnavailableException("Event service is temporarily unavailable");
        }
    }
}