package ru.practicum.controller.external;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.external.EventRichFullDto;
import ru.practicum.dto.external.EventRichShortDto;
import ru.practicum.service.EventService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventsController {

    private final EventService eventService;

    @GetMapping
    public List<EventRichShortDto> get(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10") @Positive int size,
            HttpServletRequest request) {

        return eventService.publicSearch(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                PageRequest.ofSize(size).withPage(from / size),
                request.getRequestURI(),
                request.getRemoteAddr()
        );
    }

    @GetMapping("/{id}")
    public EventRichFullDto getById(@PathVariable("id") Long id,
                                    HttpServletRequest request) {

        return eventService.getEventById(
                id,
                request.getRequestURI(),
                request.getRemoteAddr()
        );
    }
}
