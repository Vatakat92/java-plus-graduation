package ru.practicum.mapper;

import ru.practicum.dto.external.EventRichShortDto;
import ru.practicum.dto.internal.EventSummaryDto;
import ru.practicum.integration.category.dto.CategoryShortDto;
import ru.practicum.dto.external.EventRichFullDto;
import ru.practicum.dto.external.LocationDto;
import ru.practicum.integration.user.dto.UserShortDto;
import ru.practicum.model.Event;

public final class EventMapper {
    private EventMapper() {}

    public static EventRichShortDto toShortDto(
            Event e,
            UserShortDto initiator,
            CategoryShortDto category,
            Long confirmed,
            Double rating
    ) {
        return EventRichShortDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(category)
                .confirmedRequests(confirmed)
                .eventDate(e.getEventDate())
                .initiator(initiator)
                .paid(e.getPaid())
                .title(e.getTitle()).rating(rating)
                .build();
    }

    public static EventRichFullDto toFullDto(
            Event e,
            UserShortDto initiator,
            CategoryShortDto category,
            Long confirmed,
            Double rating
    ) {

        return EventRichFullDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(category)
                .confirmedRequests(confirmed)
                .createdOn(e.getCreatedOn())
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .initiator(initiator)
                .location(
                        new LocationDto(
                                e.getLocation().getLat(),
                                e.getLocation().getLon()
                        )
                )
                .paid(e.getPaid())
                .participantLimit(e.getParticipantLimit())
                .publishedOn(e.getPublishedOn())
                .requestModeration(e.getRequestModeration())
                .state(e.getState().name())
                .title(e.getTitle())
                .rating(rating)
                .build();
    }

    public static EventSummaryDto toSummaryDto(
            Event e
    ) {

        return EventSummaryDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(e.getCategoryId())
                .eventDate(e.getEventDate())
                .initiator(e.getInitiatorId())
                .paid(e.getPaid())
                .title(e.getTitle())
                .state(e.getState())
                .participantLimit(e.getParticipantLimit())
                .requestModeration(e.getRequestModeration())
                .build();
    }
}
