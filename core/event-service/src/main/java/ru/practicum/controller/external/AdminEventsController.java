package ru.practicum.controller.external;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.external.EventRichFullDto;
import ru.practicum.dto.external.UpdateEventAdminRequest;
import ru.practicum.service.EventService;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated

public class AdminEventsController {

    private final EventService eventService;

    @GetMapping
    public List<EventRichFullDto> search(@RequestParam(value="users", required=false) List<Long> users,
                                         @RequestParam(value="states", required=false) List<String> states,
                                         @RequestParam(value="categories", required=false) List<Long> categories,
                                         @RequestParam(value = "rangeStart", required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(value = "rangeEnd", required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(value="from", defaultValue="0") @PositiveOrZero int from,
                                         @RequestParam(value="size", defaultValue="10") @Positive int size) {
        return eventService.adminSearch(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                PageRequest.ofSize(size).withPage(from / size)
        );
    }

    @PatchMapping("/{eventId}")
    public EventRichFullDto patch(@PathVariable Long eventId,
                                  @Valid @RequestBody UpdateEventAdminRequest dto) {
        return eventService.adminPatchEventByEventId(
                eventId,
                dto
        );
    }
}
