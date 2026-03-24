package ru.practicum.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.clients.AnalyzerClient;
import ru.practicum.dto.external.CompilationFullDto;
import ru.practicum.dto.external.NewCompilationDto;
import ru.practicum.dto.external.UpdateCompilationDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.TitleExistsException;
import ru.practicum.integration.event.EventServiceGateway;
import ru.practicum.integration.event.dto.EnrichedEventSummaryDto;
import ru.practicum.integration.event.dto.EventSummaryDto;
import ru.practicum.integration.request.RequestServiceGateway;
import ru.practicum.integration.request.dto.RequestStatus;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationService {

    private final EventServiceGateway eventServiceGateway;
    private final RequestServiceGateway requestServiceGateway;
    private final AnalyzerClient analyzerClient;

    private final CompilationRepository compilationRepository;
    private final CompilationWriteService compilationWriteService;

    @Transactional(readOnly = true)
    public List<CompilationFullDto> getCompilations(Boolean pinned, int from, int size) {

        PageRequest pageable = PageRequest.ofSize(size).withPage(from / size);

        List<Compilation> compilations = (pinned == null)
                ? compilationRepository.findAll(pageable).getContent()
                : compilationRepository.findByPinned(pinned, pageable).getContent();

        if (compilations.isEmpty()) {
            return List.of();
        }

        List<Long> allEventIds = compilations.stream()
                .map(Compilation::getEventIds)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();

        if (allEventIds.isEmpty()) {
            return compilations.stream()
                    .map(compilation -> CompilationMapper.toFullDto(compilation, List.of()))
                    .toList();
        }

        List<EventSummaryDto> fetchedEvents = eventServiceGateway.getAllEventsById(allEventIds);

        List<Long> fetchedEventIds = fetchedEvents.stream()
                .map(EventSummaryDto::getId)
                .toList();

        Map<Long, EventSummaryDto> eventsById = fetchedEvents.stream()
                .collect(Collectors.toMap(EventSummaryDto::getId, Function.identity()));

        Map<Long, Long> confirmedMap = requestServiceGateway.getCountByEventIdsAndStatus(
                fetchedEventIds,
                RequestStatus.CONFIRMED
        );

        Map<Long, Double> scoreByEventId = analyzerClient.getInteractionsCount(fetchedEventIds);

        return compilations.stream()
                .map(compilation -> mapCompilationWithEvents(
                        compilation,
                        eventsById,
                        confirmedMap,
                        scoreByEventId)
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public CompilationFullDto getCompilationById(Long compilationId) {

        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(
                () -> new NotFoundException(
                        "Unable to get compilation. Compilation id=" + compilationId + " not found"
                )
        );

        return mapCompilationWithEvents(compilation);
    }

    public CompilationFullDto createCompilationByAdmin(NewCompilationDto dto) {

        if (compilationRepository.findByTitleIgnoreCase(dto.getTitle()).isPresent()) {
            throw new TitleExistsException(
                    "Compilation with title '" + dto.getTitle() + "' already exists"
            );
        }
        log.info("Compilation with eventsId= '{}' created.", dto.getEvents());
        return mapCompilationWithEvents(
                compilationWriteService.createCompilation(dto)
        );
    }

    public void deleteCompilationByAdmin(Long compilationId) {

        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(
                () -> new NotFoundException(
                        "Unable to delete compilation. Compilation id=" + compilationId + " not found"
                )
        );
        compilationWriteService.deleteCompilation(compilation);
    }

    public CompilationFullDto patchCompilationByAdmin(Long compilationId, UpdateCompilationDto dto) {

        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(
                () -> new NotFoundException(
                        "Unable to patch compilation. Compilation id=" + compilationId + " not found"
                )
        );

        String updatedTitle = compilation.getTitle();
        if (dto.getTitle() != null && !dto.getTitle().equals(compilation.getTitle())) {
            if (compilationRepository.findByTitleIgnoreCase(dto.getTitle()).isPresent()) {
                throw new TitleExistsException(
                        "Unable to patch compilation. Compilation with title '" + dto.getTitle() + "' already exists"
                );
            }
            updatedTitle = dto.getTitle();
        }

        return mapCompilationWithEvents(
                compilationWriteService.patchCompilation(compilation, dto, updatedTitle)
        );
    }

    private CompilationFullDto mapCompilationWithEvents(Compilation compilation) {
        List<Long> eventIds = compilation.getEventIds();

        if (eventIds == null || eventIds.isEmpty()) {
            return CompilationMapper.toFullDto(compilation, List.of());
        }

        List<EventSummaryDto> fetchedEvents = eventServiceGateway.getAllEventsById(eventIds);

        List<Long> fetchedEventIds = fetchedEvents.stream()
                .map(EventSummaryDto::getId)
                .toList();

        Map<Long, Long> confirmedMap = requestServiceGateway.getCountByEventIdsAndStatus(
                        fetchedEventIds,
                        RequestStatus.CONFIRMED
                );

        Map<Long, Double> scoreByEventId = analyzerClient.getInteractionsCount(fetchedEventIds);

        List<EnrichedEventSummaryDto> events = fetchedEvents.stream()
                .map(event -> enrichEvent(
                        event,
                        confirmedMap,
                        scoreByEventId)
                )
                .toList();

        return CompilationMapper.toFullDto(compilation, events);
    }

    private CompilationFullDto mapCompilationWithEvents(
            Compilation compilation,
            Map<Long, EventSummaryDto> eventsById,
            Map<Long, Long> confirmedMap,
            Map<Long, Double> scoreByEventId
    ) {
        List<Long> eventIds = compilation.getEventIds();

        if (eventIds == null || eventIds.isEmpty()) {
            return CompilationMapper.toFullDto(compilation, List.of());
        }

        List<EnrichedEventSummaryDto> events = eventIds.stream()
                .map(eventsById::get)
                .filter(Objects::nonNull)
                .map(event -> enrichEvent(
                        event,
                        confirmedMap,
                        scoreByEventId)
                )
                .toList();

        return CompilationMapper.toFullDto(compilation, events);
    }

    private EnrichedEventSummaryDto enrichEvent(
            EventSummaryDto event,
            Map<Long, Long> confirmedMap,
            Map<Long, Double> scoreByEventId
    ) {
        Long eventId = event.getId();

        return EnrichedEventSummaryDto.builder()
                .id(eventId)
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .confirmedRequests(confirmedMap.getOrDefault(eventId, 0L))
                .rating(scoreByEventId.getOrDefault(eventId, 0D))
                .build();
    }

}
