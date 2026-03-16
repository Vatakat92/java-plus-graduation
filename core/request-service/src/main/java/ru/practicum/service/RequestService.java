package ru.practicum.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.external.RequestUpdateDto;
import ru.practicum.dto.external.RequestUpdateRespondDto;
import ru.practicum.dto.external.ParticipationRequestDto;
import ru.practicum.dto.internal.RequestShortDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.integration.event.EventServiceGateway;
import ru.practicum.integration.event.dto.EventState;
import ru.practicum.integration.event.dto.EventSummaryDto;
import ru.practicum.integration.user.UserServiceGateway;
import ru.practicum.integration.user.dto.UserShortDto;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;
import ru.practicum.repository.RequestRepository;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final EventServiceGateway eventServiceGateway;
    private final UserServiceGateway userServiceGateway;
    private final RequestRepository requestRepository;
    private final RequestWriteService requestWriteService;

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {

        UserShortDto user = userServiceGateway.getUserById(userId);

        return requestRepository.findAllByRequesterId(user.getId()).stream()
                .map(RequestMapper::toFullDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByEventId(Long userId, Long eventId) {

        EventSummaryDto event = eventServiceGateway.getEventById(eventId);

        if (!event.getInitiator().equals(userId)) {
            throw new NotFoundException(
                    "Event with id=" + eventId + " found, but has different initiator"
            );
        }

        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toFullDto)
                .toList();
    }

    public ParticipationRequestDto createRequest(Long userId, Long eventId) {

        UserShortDto user = userServiceGateway.getUserById(userId);
        EventSummaryDto event = eventServiceGateway.getEventById(eventId);

        if (requestRepository.existsByRequesterIdAndEventId(userId, event.getId())) {
            throw new IllegalStateException(
                    "Request already exists."
            );
        }

        if (event.getInitiator().equals(userId)) {
            throw new IllegalStateException(
                    "Initiator cannot send participation request."
            );
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new IllegalStateException(
                    "Cannot participate in unpublished event."
            );
        }

        if (event.getParticipantLimit() > 0) {
            Long confirmed = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
            if (confirmed >= event.getParticipantLimit()) {
                throw new IllegalStateException(
                        "The participant limit has been reached."
                );
            }
        }

        return requestWriteService.createRequest(user, event);
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        UserShortDto user = userServiceGateway.getUserById(userId);

        ParticipationRequest request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(
                            "Request with id=" + requestId + " was not found."
                )
        );

        if (!request.getRequesterId().equals(user.getId())) {
            throw new IllegalStateException(
                    "Requester mismatch for request id=" + requestId
            );
        }

        return requestWriteService.cancelRequest(request);
    }

    public RequestUpdateRespondDto updateEventRequests(
            Long userId,
            Long eventId,
            RequestUpdateDto dto
    ) {
        if (dto == null || dto.getRequestIds() == null || dto.getRequestIds().isEmpty()) {
            return RequestUpdateRespondDto.builder()
                    .confirmedRequests(List.of())
                    .rejectedRequests(List.of())
                    .build();
        }

        UserShortDto user = userServiceGateway.getUserById(userId);
        EventSummaryDto event = eventServiceGateway.getEventById(eventId);

        if (!event.getInitiator().equals(user.getId())) {
            throw new IllegalStateException(
                    "Event with id=" + eventId + " found, but has different initiator."
            );
        }

        if (dto.getStatus() != RequestStatus.CONFIRMED && dto.getStatus() != RequestStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Request status must be CONFIRMED or REJECTED. Current value: " + dto.getStatus()
            );
        }

        Long alreadyConfirmed = requestRepository.countByEventIdAndStatus(
                event.getId(),
                RequestStatus.CONFIRMED
        );

        if (dto.getStatus() == RequestStatus.CONFIRMED
                && event.getParticipantLimit() > 0
                && alreadyConfirmed >= event.getParticipantLimit()) {
            throw new IllegalStateException(
                    "The participant limit has been reached."
            );
        }

        Long capacity = (event.getParticipantLimit() == 0) ?
                Long.MAX_VALUE :
                Math.max(0, event.getParticipantLimit() - alreadyConfirmed);

        List<ParticipationRequest> requestsUpdateList = requestRepository.findAllById(dto.getRequestIds())
                .stream()
                .filter(r -> r.getEventId().equals(event.getId()))
                .toList();

        return requestWriteService.updateRequests(capacity, dto, requestsUpdateList);
    }

    public Map<Long, Long> getCountByEventIdsAndStatus(List<Long> eventIds, RequestStatus status) {
        return requestRepository.findAllByEventIdInAndStatus(eventIds, status)
                .stream()
                .collect(Collectors.groupingBy(
                        ParticipationRequest::getEventId,
                        Collectors.counting()
                ));
    }

    public Long getCountByEventIdAndStatus(Long eventId, RequestStatus status) {
        return requestRepository.countByEventIdAndStatus(eventId, status);
    }

    public RequestShortDto getByRequesterById(Long userId, Long eventId) {
        return requestRepository.findByRequesterIdAndEventId(userId, eventId)
                .map(RequestMapper::toShortDto)
                .orElseThrow(
                        () -> new NotFoundException(
                                "Request with userId=" + userId + " and eventId=" + eventId + " not found."
                        )
                );
    }
}
