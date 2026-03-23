package ru.practicum.service;

import static ru.practicum.clients.ActionType.ACTION_LIKE;
import static ru.practicum.clients.ActionType.ACTION_VIEW;
import static ru.practicum.model.EventState.CANCELED;
import static ru.practicum.model.EventState.PUBLISHED;
import static ru.practicum.model.StateAction.PUBLISH_EVENT;
import static ru.practicum.model.StateAction.REJECT_EVENT;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.clients.AnalyzerClient;
import ru.practicum.clients.CollectorClient;
import ru.practicum.dto.external.EventRichShortDto;
import ru.practicum.dto.external.UpdateEventAdminRequest;
import ru.practicum.dto.internal.EventSummaryDto;
import ru.practicum.integration.category.dto.CategoryShortDto;
import ru.practicum.dto.external.EventRichFullDto;
import ru.practicum.dto.external.NewEventDto;
import ru.practicum.dto.external.UpdateEventPublicRequest;
import ru.practicum.integration.user.dto.UserShortDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.integration.category.CategoryServiceGateway;
import ru.practicum.integration.request.RequestServiceGateway;
import ru.practicum.integration.request.dto.RequestStatus;
import ru.practicum.integration.user.UserServiceGateway;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.repository.EventRepository;

@Service
@RequiredArgsConstructor
public class EventService {

    private final UserServiceGateway userServiceGateway;
    private final CategoryServiceGateway categoryServiceGateway;
    private final RequestServiceGateway requestServiceGateway;
    private final AnalyzerClient analyzerClient;
    private final CollectorClient collectorClient;

    private final EventRepository eventRepository;
    private final EventWriteService eventWriteService;

    // External API
    @Transactional(readOnly = true)
    public List<EventRichShortDto> publicSearch(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime start,
            LocalDateTime end,
            Boolean onlyAvailable,
            String sort,
            Pageable pageable
    ) {
        validateRangeOrThrow(start, end);

        Specification<Event> eventSpecification = (root, q, cb)
                -> cb.equal(
                        root.get("state"), PUBLISHED
        );

        if (text != null && !text.isBlank()) {
            String pattern = "%" + text.toLowerCase() + "%";
            eventSpecification = eventSpecification.and(
                    (root, q, cb)
                    -> cb.or(cb.like(
                            cb.lower(root.get("annotation")), pattern),
                            cb.like(cb.lower(root.get("description")), pattern)
                    )
            );
        }

        if (paid != null) {
            eventSpecification = eventSpecification.and(
                    (root, q, cb)
                    -> cb.equal(
                            root.get("paid"), paid)
            );
        }

        if (categories != null && !categories.isEmpty()) {
            eventSpecification = eventSpecification.and(
                    (root, q, cb)
                    -> root.get("categoryId").in(categories)
            );
        }

        if (end != null) {
            eventSpecification = eventSpecification.and(
                    (root, q, cb)
                            -> cb.lessThanOrEqualTo(root.get("eventDate"), end)
            );
        }

        if (start != null) {
            eventSpecification = eventSpecification.and(
                    (root, q, cb)
                            -> cb.greaterThanOrEqualTo(root.get("eventDate"), start)
            );
        } else if (end == null) {
            eventSpecification = eventSpecification.and(
                    (root, q, cb)
                            -> cb.greaterThanOrEqualTo(root.get("eventDate"), LocalDateTime.now())
            );
        }

        // Load candidate events matching local filters.
        List<Event> allEvents = eventRepository.findAll(eventSpecification);
        List<Long> allEventIds = allEvents.stream()
                .map(Event::getId)
                .toList();

        // Batch load confirmed counts from request-service
        Map<Long, Long> confirmedByEventId =
                requestServiceGateway.getCountByEventIdsAndStatus(allEventIds, RequestStatus.CONFIRMED);

        // if onlyAvailable -> filter by participantLimit/count
        if (Boolean.TRUE.equals(onlyAvailable)) {
            allEvents = allEvents.stream()
                    .filter(e -> e.getParticipantLimit() == 0
                            || confirmedByEventId.getOrDefault(e.getId(), 0L) < e.getParticipantLimit())
                    .toList();

            allEventIds = allEvents.stream()
                    .map(Event::getId)
                    .toList();
        }

        // Batch load rating from analyzer(stats-service)
        Map<Long, Double> scoreByEventId = analyzerClient.getInteractionsCount(allEventIds);

        // Bach load user from user-service
        List<Long> allEventsUserIds = allEvents.stream()
                .map(Event::getInitiatorId)
                .toList();
        Map<Long, UserShortDto> usersById = userServiceGateway.getUserByIds(allEventsUserIds);

        // Bach load category from category-service
        List<Long> allEventsCategoryIds = allEvents.stream()
                .map(Event::getCategoryId)
                .toList();
        Map<Long, CategoryShortDto> categoriesById = categoryServiceGateway.getCategoriesByIds(allEventsCategoryIds);

        // Sort
        List<Event> sortedEvents;
        if ("VIEWS".equalsIgnoreCase(sort)) {
            sortedEvents = allEvents.stream()
                    .sorted(Comparator.comparingDouble(
                            (Event e) -> scoreByEventId.getOrDefault(e.getId(), 0D)
                    ).reversed())
                    .toList();
        } else {
            sortedEvents = allEvents.stream()
                    .sorted(Comparator.comparing(Event::getEventDate))
                    .toList();
        }

        // Paginate
        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), sortedEvents.size());

        if (fromIndex >= sortedEvents.size()) {
            return List.of();
        }

        List<Event> pagedEvents = sortedEvents.subList(fromIndex, toIndex);

        // Return paged event list to ShortDTO
        return pagedEvents.stream()
                .map(e -> EventMapper.toShortDto(
                        e,
                        usersById.get(e.getInitiatorId()),
                        categoriesById.get(e.getCategoryId()),
                        confirmedByEventId.getOrDefault(e.getId(), 0L),
                        scoreByEventId.getOrDefault(e.getId(), 0D)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public EventRichFullDto getEventById(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(
                        "Event with id=" + eventId + " was not found"
                )
        );

        if (event.getState() != PUBLISHED) {
            throw new NotFoundException(
                    "Event with id=" + eventId + " found but not PUBLISHED."
            );
        }

        CategoryShortDto categoryShortDto = categoryServiceGateway.getCategoryById(event.getCategoryId());
        UserShortDto userShortDto = userServiceGateway.getUserById(event.getInitiatorId());
        Long confirmedRequest = requestServiceGateway.getCountByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        Double rating = getScoreForEventId(event.getId());

        collectorClient.collectUserActions(userId, eventId, ACTION_VIEW);

        return EventMapper.toFullDto(
                event,
                userShortDto,
                categoryShortDto,
                confirmedRequest,
                rating
        );
    }

    @Transactional(readOnly = true)
    public List<EventRichShortDto> getEventsByUserId(Long userId, Pageable pageable) {

        UserShortDto userShortDto = userServiceGateway.getUserById(userId);

        Page<Event> page = eventRepository.findAllByInitiatorId(
                userId,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by("createdOn").descending()
                )
        );

        List<Event> events = page.getContent();
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        // Batch load rating from analyzer(stats-service)
        Map<Long, Double> scoreByEventId = analyzerClient.getInteractionsCount(eventIds);

        // Batch load confirmed counts from request-service
        Map<Long, Long> confirmedRequestCountsByEventId =
                requestServiceGateway.getCountByEventIdsAndStatus(eventIds, RequestStatus.CONFIRMED);

        // Bach load category from category-service
        List<Long> eventsCategoryIds = events.stream()
                .map(Event::getCategoryId)
                .toList();
        Map<Long, CategoryShortDto> categoryShortDtoMap = categoryServiceGateway.getCategoriesByIds(eventsCategoryIds);

        return events.stream()
                .map(
                    e -> EventMapper.toShortDto(
                        e,
                        userShortDto,
                        categoryShortDtoMap.get(e.getCategoryId()),
                        confirmedRequestCountsByEventId.getOrDefault(e.getId(), 0L),
                        scoreByEventId.getOrDefault(e.getId(), 0D)
                    )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public void likeEventById(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(
                        "Event with id=" + eventId + " was not found."
                )
        );

        if (event.getState() != PUBLISHED) {
            throw new IllegalArgumentException(
                    "Can't set LIKE an unpublished event."
            );
        }

        if (!requestServiceGateway.hasUserIdAttendEventId(userId, eventId)) {
            throw new IllegalArgumentException(
                    "The user did not attend the event."
            );
        }

        collectorClient.collectUserActions(userId, eventId, ACTION_LIKE);
    }

    @Transactional(readOnly = true)
    public List<EventRichShortDto> getRecommendationsForUserId(Long userId, int size) {

        Map<Long, Double> recommendationEventsAndScore = analyzerClient.getRecommendationsForUserId(userId, size);

        // Batch load event data
        List<Event> events = eventRepository.findAllById(recommendationEventsAndScore.keySet());

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        // Batch load confirmed counts from request-service
        Map<Long, Long> confirmedRequestCountsByEventId =
                requestServiceGateway.getCountByEventIdsAndStatus(eventIds, RequestStatus.CONFIRMED);

        // Bach load category from category-service
        List<Long> eventsCategoryIds = events.stream()
                .map(Event::getCategoryId)
                .toList();
        Map<Long, CategoryShortDto> categoryShortDtoMap = categoryServiceGateway.getCategoriesByIds(eventsCategoryIds);

        // Bach load user from user-service
        List<Long> allEventsUserIds = events.stream()
                .map(Event::getInitiatorId)
                .toList();
        Map<Long, UserShortDto> usersShortDtoMap = userServiceGateway.getUserByIds(allEventsUserIds);

        return events.stream()
                .map(
                        e -> EventMapper.toShortDto(
                                e,
                                usersShortDtoMap.get(e.getInitiatorId()),
                                categoryShortDtoMap.get(e.getCategoryId()),
                                confirmedRequestCountsByEventId.getOrDefault(e.getId(), 0L),
                                recommendationEventsAndScore.getOrDefault(e.getId(), 0D)
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public EventRichFullDto getEventByUserId(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(
                        "Event with id=" + eventId + " was not found"
                )
        );

        if (!event.getInitiatorId().equals(userId)) {
            throw new NotFoundException(
                    "Event with id=" + eventId + " found, but has different initiator"
            );
        }

        CategoryShortDto category = categoryServiceGateway.getCategoryById(event.getCategoryId());
        UserShortDto initiator = userServiceGateway.getUserById(event.getInitiatorId());
        Long confirmedRequest = requestServiceGateway.getCountByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        Double rating = getScoreForEventId(event.getId());

        return EventMapper.toFullDto(
                event,
                initiator,
                category,
                confirmedRequest,
                rating
        );
    }

    public EventRichFullDto addEventByUserId(Long userId, NewEventDto dto) {

        if (dto.getEventDate() == null || dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))  {
            throw new IllegalArgumentException(
                    "Event date must be at least 2 hours in the future"
            );
        }

        if (dto.getParticipantLimit() < 0) {
            throw new IllegalArgumentException(
                    "participantLimit must be >= 0"
            );
        }

        UserShortDto initiator = userServiceGateway.getUserById(userId);
        CategoryShortDto category = categoryServiceGateway.getCategoryById(dto.getCategory());
        Event saved = eventWriteService.createEvent(initiator, category, dto);

        return EventMapper.toFullDto(
                saved,
                initiator,
                category,
                0L,
                0D
        );
    }

    public EventRichFullDto publicPatchEventByUserId(Long userId, Long eventId, UpdateEventPublicRequest dto) {

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(
                        "Event with id=" + eventId + " was not found."
                )
        );

        if (dto.getCategory() == null || event.getCategoryId().equals(dto.getCategory())) {
            dto.setCategory(event.getCategoryId());
        }
        CategoryShortDto category = categoryServiceGateway.getCategoryById(dto.getCategory());
        UserShortDto initiator = userServiceGateway.getUserById(userId);

        if (!event.getInitiatorId().equals(initiator.getId())) {
            throw new NotFoundException(
                    "Event with id=" + eventId + " found, but has different initiator"
            );
        }

        if (event.getState() == PUBLISHED) {
            throw new IllegalStateException(
                    "Only pending or canceled events can be changed"
            );
        }

        if (dto.getEventDate() != null) {
            if (!dto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
                throw new IllegalArgumentException(
                        "Event date must be at least 2 hours in the future"
                );
            }
        }

        if (dto.getParticipantLimit() != null && dto.getParticipantLimit() < 0) {
            throw new IllegalArgumentException(
                    "participantLimit must be >= 0"
            );
        }

        Long confirmedRequest = requestServiceGateway.getCountByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        Double rating = getScoreForEventId(event.getId());

        Event patched = eventWriteService.patchEvent(event, dto);

        return EventMapper.toFullDto(
                patched,
                initiator,
                category,
                confirmedRequest,
                rating
        );
    }

    public List<EventRichFullDto> adminSearch(List<Long> users,
                                              List<String> states,
                                              List<Long> categories,
                                              LocalDateTime start,
                                              LocalDateTime end,
                                              Pageable pageable) {

        validateRangeOrThrow(start, end);

        Specification<Event> eventSpecification = Specification.where(null);

        if (users != null && !users.isEmpty()) {
            eventSpecification = eventSpecification.and(
                    (root, q, cb)
                            -> root.get("initiatorId").in(users)
            );
        }

        if (categories != null && !categories.isEmpty()) {
            eventSpecification = eventSpecification.and(
                    (root, q, cb)
                            -> root.get("categoryId").in(categories)
            );
        }

        if (states != null && !states.isEmpty()) {
            List<EventState> eventStates = states.stream()
                    .map(String::toUpperCase)
                    .map(EventState::valueOf)
                    .toList();

            eventSpecification = eventSpecification.and(
                    (root, q, cb) ->
                            root.get("state").in(eventStates)
            );
        }

        if (start != null) {
            eventSpecification = eventSpecification.and(
                    (root, q, cb)
                            -> cb.greaterThanOrEqualTo(root.get("eventDate"), start)
            );
        }

        if (end != null) {
            eventSpecification = eventSpecification.and(
                    (root, q, cb)
                            -> cb.lessThanOrEqualTo(root.get("eventDate"), end)
            );
        }

        Page<Event> page = eventRepository.findAll(
                eventSpecification,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by("eventDate").ascending()
                )
        );

        List<Event> events = page.getContent();

        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        List<Long> userIds = events.stream()
                .map(Event::getInitiatorId)
                .toList();

        List<Long> categoryIds = events.stream()
                .map(Event::getCategoryId)
                .toList();

        // Batch load rating from analyzer(stats-service)
        Map<Long, Double> scoreByEventId = analyzerClient.getInteractionsCount(eventIds);

        // Batch load confirmed counts from request-service
        Map<Long, Long> confirmedByEventId =
                requestServiceGateway.getCountByEventIdsAndStatus(eventIds, RequestStatus.CONFIRMED);

        // Bach load user from user-service
        Map<Long, UserShortDto> usersById = userServiceGateway.getUserByIds(userIds);

        // Batch load category from category-service
        Map<Long, CategoryShortDto> categoriesById =
                categoryServiceGateway.getCategoriesByIds(categoryIds);

        // Return event list for convert to FullDTO
        return events.stream()
                .map(e -> EventMapper.toFullDto(
                        e,
                        usersById.get(e.getInitiatorId()),
                        categoriesById.get(e.getCategoryId()),
                        confirmedByEventId.getOrDefault(e.getId(), 0L),
                        scoreByEventId.getOrDefault(e.getId(), 0D)
                ))
                .toList();
    }

    public EventRichFullDto adminPatchEventByEventId(Long eventId, UpdateEventAdminRequest dto) {

        if (dto.getEventDate() != null) {
            if (!dto.getEventDate().isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException(
                        "Event date must be in the future."
                );
            }
        }

        if (dto.getParticipantLimit() != null && dto.getParticipantLimit() < 0) {
            throw new IllegalArgumentException(
                    "participantLimit must be >= 0"
            );
        }

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(
                        "Event with id=" + eventId + " was not found."
                )
        );

        LocalDateTime publishedOn = event.getPublishedOn();
        EventState state = event.getState();

        if (dto.getStateAction() == PUBLISH_EVENT) {

            publishedOn = LocalDateTime.now();

            if (event.getState() != EventState.PENDING) {
                throw new IllegalStateException(
                        "Cannot publish the event because it's not in the right state: " + event.getState()
                );
            }

            if (event.getEventDate().isBefore(publishedOn.plusHours(1))) {
                throw new IllegalStateException(
                        "Event date must be at least 1 hour after publication"
                );
            }
            state = PUBLISHED;

        } else if (dto.getStateAction() == REJECT_EVENT) {
            if (event.getState() == PUBLISHED) {
                throw new IllegalStateException(
                        "Cannot reject the event because it's already published"
                );
            }
            state = CANCELED;
        }

        Long categoryId = event.getCategoryId();
        if (dto.getCategory() != null && !dto.getCategory().equals(categoryId)) {
            categoryId = dto.getCategory();
        }

        CategoryShortDto category = categoryServiceGateway.getCategoryById(categoryId);
        UserShortDto initiator = userServiceGateway.getUserById(event.getInitiatorId());
        Long confirmedRequest = requestServiceGateway.getCountByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        Double rating = getScoreForEventId(event.getId());

        Event patched = eventWriteService.patchEvent(event, dto, categoryId, publishedOn, state);

        return EventMapper.toFullDto(
                patched,
                initiator,
                category,
                confirmedRequest,
                rating
        );
    }


    // Internal API
    @Transactional(readOnly = true)
    public EventSummaryDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(
                        "Event with id=" + eventId + " was not found."
                )
        );
        return EventMapper.toSummaryDto(event);
    }

    @Transactional(readOnly = true)
    public Boolean existsByCategoryId(Long categoryId) {
        return eventRepository.existsByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<EventSummaryDto> getEventsByIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }

        return eventRepository.findAllById(eventIds).stream()
                .map(EventMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    private void validateRangeOrThrow(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("Date end must be after start date.");
        }
    }

    private Double getScoreForEventId(Long eventId) {
        Map<Long, Double> scores = analyzerClient.getInteractionsCount(List.of(eventId));
        return scores.getOrDefault(eventId, 0D);
    }
}