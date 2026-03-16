package ru.practicum.controller.internal;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.internal.EventIdBatchRequest;
import ru.practicum.dto.internal.EventSummaryDto;
import ru.practicum.service.EventService;

@RestController
@RequestMapping("/internal/events")
@RequiredArgsConstructor
public class InternalEventsController {

    private final EventService eventService;

    @GetMapping("/by-id/{eventId}")
    public EventSummaryDto getEventById(@PathVariable Long eventId) {
        return eventService.getEventById(eventId);
    }

    @GetMapping("/by-category-id/{categoryId}")
    public Boolean existsByCategoryId(@PathVariable Long categoryId) {
        return eventService.existsByCategoryId(categoryId);
    }

    @PostMapping("/by-ids")
    public List<EventSummaryDto> getAllEventsById(@RequestBody EventIdBatchRequest requests) {
        return eventService.getEventsByIds(requests.ids());
    }
}
