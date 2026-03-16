package ru.practicum.integration.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ServiceUnavailableException;
import ru.practicum.integration.request.dto.RequestShortDto;
import ru.practicum.integration.request.exceptions.RequestNotFoundException;
import ru.practicum.integration.request.exceptions.RequestServiceUnavailableException;

@Component
@RequiredArgsConstructor
public class RequestServiceGateway {

    private final RequestServiceClient requestClient;

    public RequestShortDto getByRequesterById(Long userId, Long eventId) {
        try {
            return requestClient.getByRequesterById(userId, eventId);
        } catch (RequestNotFoundException e) {
            throw new NotFoundException("Requester with userId=" + userId + " and eventId=" + eventId + " was not found");
        } catch (RequestServiceUnavailableException e) {
            throw new ServiceUnavailableException("Request service is temporarily unavailable");
        }
    }
}