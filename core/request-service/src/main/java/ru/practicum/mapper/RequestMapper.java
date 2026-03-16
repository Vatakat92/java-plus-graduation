package ru.practicum.mapper;

import ru.practicum.dto.external.ParticipationRequestDto;
import ru.practicum.dto.internal.RequestShortDto;
import ru.practicum.model.ParticipationRequest;

public final class RequestMapper {

    private RequestMapper() {}

    public static ParticipationRequestDto toFullDto(
            ParticipationRequest r
    ) {
        return ParticipationRequestDto.builder()
                .id(r.getId())
                .created(r.getCreated())
                .event(r.getEventId())
                .requester(r.getRequesterId())
                .status(r.getStatus())
                .build();
    }

    public static RequestShortDto toShortDto(
            ParticipationRequest r
    ) {
        return RequestShortDto.builder()
                .requestId(r.getId())
                .status(r.getStatus())
                .build();
    }
}