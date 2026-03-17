package ru.practicum.integration.request;

import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ServiceUnavailableException;
import ru.practicum.integration.request.dto.BatchCountEventIdandStatus;
import ru.practicum.integration.request.dto.RequestStatus;
import ru.practicum.integration.request.exceptions.RequestNotFoundException;
import ru.practicum.integration.request.exceptions.RequestServiceUnavailableException;

@Component
@RequiredArgsConstructor
public class RequestServiceGateway {

    private final RequestServiceClient requestClient;

    public Map<Long, Long> getCountByEventIdsAndStatus(Set<Long> eventIds, RequestStatus status) {

        if (eventIds == null || eventIds.isEmpty()) {
            return Map.of();
        }

        try {
            return requestClient.getCountByEventIdsAndStatus(new BatchCountEventIdandStatus(eventIds, status));
        } catch (RequestNotFoundException e) {
            throw new NotFoundException("Requests for eventId=" + eventIds + " and status=" + status + " was not found");
        } catch (RequestServiceUnavailableException e) {
            throw new ServiceUnavailableException("Request service is temporarily unavailable");
        }
    }
}