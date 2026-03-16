package ru.practicum.controller.external;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.external.RequestUpdateDto;
import ru.practicum.dto.external.RequestUpdateRespondDto;
import ru.practicum.dto.external.ParticipationRequestDto;
import ru.practicum.service.RequestService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class PrivateEventRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> list(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        return requestService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping
    public RequestUpdateRespondDto update(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @RequestBody RequestUpdateDto dto) {
        return requestService.updateEventRequests(userId, eventId, dto);
    }
}