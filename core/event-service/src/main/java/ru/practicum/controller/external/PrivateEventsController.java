package ru.practicum.controller.external;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.external.EventRichFullDto;
import ru.practicum.dto.external.EventRichShortDto;
import ru.practicum.dto.external.NewEventDto;
import ru.practicum.dto.external.UpdateEventPublicRequest;
import ru.practicum.service.EventService;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated

public class PrivateEventsController {

    private final EventService eventService;

    @GetMapping
    public List<EventRichShortDto> getUserEvents(@PathVariable Long userId,
                                                 @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        return eventService.getEventsByUserId(
                userId,
                PageRequest.ofSize(size).withPage(from / size)
        );
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public EventRichFullDto addEvent(@PathVariable Long userId,
                                     @Valid @RequestBody NewEventDto dto) {
        return eventService.addEventByUserId(
                userId,
                dto
        );
    }

    @GetMapping("/{eventId}")
    public EventRichFullDto getEventByUser(@PathVariable Long userId,
                                           @PathVariable Long eventId) {
        return eventService.getEventByUserId(
                userId,
                eventId
        );
    }

    @PatchMapping("/{eventId}")
    public EventRichFullDto patchEventByUser(@PathVariable Long userId,
                                             @PathVariable Long eventId,
                                             @Valid @RequestBody UpdateEventPublicRequest dto) {
        return eventService.publicPatchEventByUserId(
                userId,
                eventId,
                dto
        );
    }
}
