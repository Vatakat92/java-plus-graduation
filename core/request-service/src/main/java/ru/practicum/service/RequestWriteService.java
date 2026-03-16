package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.external.ParticipationRequestDto;
import ru.practicum.dto.external.RequestUpdateDto;
import ru.practicum.dto.external.RequestUpdateRespondDto;
import ru.practicum.integration.event.dto.EventSummaryDto;
import ru.practicum.integration.user.dto.UserShortDto;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;
import ru.practicum.repository.RequestRepository;

@Service
@RequiredArgsConstructor
public class RequestWriteService {

    private final RequestRepository requestRepository;

    @Transactional
    public ParticipationRequestDto createRequest(UserShortDto user, EventSummaryDto event) {

        ParticipationRequest request = ParticipationRequest.builder()
                .requesterId(user.getId())
                .eventId(event.getId())
                .created(LocalDateTime.now())
                .status(
                        event.getParticipantLimit() == 0 || !event.getRequestModeration()
                                ? RequestStatus.CONFIRMED : RequestStatus.PENDING
                )
                .build();

        return RequestMapper.toFullDto(
                requestRepository.save(request)
        );
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(ParticipationRequest request) {

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toFullDto(
                requestRepository.save(request)
        );
    }

    @Transactional
    public RequestUpdateRespondDto updateRequests(
            Long capacity,
            RequestUpdateDto dto,
            List<ParticipationRequest> requests
    ) {

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests  = new ArrayList<>();

        for (ParticipationRequest r : requests) {
            if (r.getStatus() != RequestStatus.PENDING) {
                throw new IllegalStateException(
                        "Can only change request with the PENDING status."
                );
            }

            if (dto.getStatus() == RequestStatus.REJECTED) {
                r.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(RequestMapper.toFullDto(r));
            } else {
                if (capacity > 0) {
                    r.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(RequestMapper.toFullDto(r));
                    capacity--;
                } else {
                    r.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(RequestMapper.toFullDto(r));
                }
            }
        }

        requestRepository.saveAll(requests);

        return RequestUpdateRespondDto.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }
}
