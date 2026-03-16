package ru.practicum.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.external.UpdateEventAdminRequest;
import ru.practicum.integration.category.dto.CategoryShortDto;
import ru.practicum.dto.external.NewEventDto;
import ru.practicum.dto.external.UpdateEventPublicRequest;
import ru.practicum.integration.user.dto.UserShortDto;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;
import ru.practicum.repository.EventRepository;

@Service
@RequiredArgsConstructor
public class EventWriteService {

    private final EventRepository eventRepository;

    @Transactional
    public Event createEvent(UserShortDto initiator, CategoryShortDto category, NewEventDto dto) {
        Event event = Event.builder()
                        .annotation(dto.getAnnotation())
                        .categoryId(category.getId())
                        .initiatorId(initiator.getId())
                        .description(dto.getDescription())
                        .location(new Location(dto.getLocation().getLat(), dto.getLocation().getLon()))
                        .paid(dto.isPaid())
                        .participantLimit(dto.getParticipantLimit())
                        .requestModeration(dto.isRequestModeration())
                        .eventDate(dto.getEventDate())
                        .createdOn(LocalDateTime.now())
                        .state(EventState.PENDING)
                        .title(dto.getTitle())
                        .build();

        return  eventRepository.save(event);
    }

    @Transactional
    public Event patchEvent(Event event,
                            UpdateEventPublicRequest dto) {

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategoryId(dto.getCategory());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocation(new Location(
                    dto.getLocation().getLat(),
                    dto.getLocation().getLon()
            ));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }

        if ("SEND_TO_REVIEW".equalsIgnoreCase(dto.getStateAction())) {
            event.setState(EventState.PENDING);
        }
        if ("CANCEL_REVIEW".equalsIgnoreCase(dto.getStateAction())) {
            event.setState(EventState.CANCELED);
        }

        return eventRepository.save(event);
    }

    @Transactional
    public Event patchEvent(Event event,
                            UpdateEventAdminRequest dto,
                            Long categoryId,
                            LocalDateTime publishedOn,
                            EventState state) {

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (categoryId != null) {
            event.setCategoryId(categoryId);
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocation(new Location(
                    dto.getLocation().getLat(),
                    dto.getLocation().getLon()
            ));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (publishedOn != null) {
            event.setPublishedOn(publishedOn);
        }
        if (state != null) {
            event.setState(state);
        }
        return eventRepository.save(event);
    }


}
