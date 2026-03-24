package ru.practicum.integration.request;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ServiceUnavailableException;
import ru.practicum.integration.request.dto.BatchCountEventIdAndStatus;
import ru.practicum.integration.request.dto.RequestStatus;
import ru.practicum.integration.request.exceptions.RequestNotFoundException;
import ru.practicum.integration.request.exceptions.RequestServiceUnavailableException;

@Component
@RequiredArgsConstructor
public class RequestServiceGateway {

    private final RequestServiceClient requestClient;

    public Map<Long, Long> getCountByEventIdsAndStatus(List<Long> eventIds, RequestStatus status) {

        if (eventIds == null || eventIds.isEmpty()) {
            return Map.of();
        }

        try {
            return requestClient.getCountByEventIdsAndStatus(new BatchCountEventIdAndStatus(eventIds, status));
        } catch (RequestNotFoundException e) {
            throw new NotFoundException("Requests for eventId=" + eventIds + " and status=" + status + " was not found");
        } catch (RequestServiceUnavailableException e) {
            throw new ServiceUnavailableException("Request service is temporarily unavailable");
        }
    }

    public Long getCountByEventIdAndStatus(Long eventId, RequestStatus status) {
        try {
            return requestClient.getCountByEventIdAndStatus(eventId, status);
        } catch (RequestNotFoundException e) {
            throw new NotFoundException("Requests for eventId=" + eventId + " and status=" + status + " was not found");
        } catch (RequestServiceUnavailableException e) {
            throw new ServiceUnavailableException("Request service is temporarily unavailable");
        }
    }

    public Boolean hasUserIdAttendEventId(Long eventId, Long userId) {
        try {
            return requestClient.hasUserIdAttendEventId(eventId, userId);
        } catch (RequestNotFoundException e) {
            throw new NotFoundException("Attend requests for userId=" + userId + " for eventId=" + eventId + " was not found");
        } catch (RequestServiceUnavailableException e) {
            throw new ServiceUnavailableException("Request service is temporarily unavailable");
        }
    }
}