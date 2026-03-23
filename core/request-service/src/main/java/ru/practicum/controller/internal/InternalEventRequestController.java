package ru.practicum.controller.internal;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.internal.BatchCountEventIdAndStatus;
import ru.practicum.dto.internal.RequestShortDto;
import ru.practicum.model.RequestStatus;
import ru.practicum.service.RequestService;

@RestController
@RequestMapping("/internal/requests")
@RequiredArgsConstructor
public class InternalEventRequestController {

    private final RequestService requestService;

    @PostMapping("/count-by-ids")
    public Map<Long, Long> getCountByEventIdsAndStatus(@RequestBody BatchCountEventIdAndStatus requesters) {
        return requestService.getCountByEventIdsAndStatus(requesters.eventIds(), requesters.status());
    }

    @GetMapping("/count-by-id/{eventId}/status/{status}")
    public Long getCountByEventIdAndStatus(@PathVariable Long eventId,
                                           @PathVariable RequestStatus status) {
        return requestService.getCountByEventIdAndStatus(eventId, status);
    }

    @GetMapping("/userid/{userId}/eventid/{eventId}")
    public RequestShortDto getByRequesterById(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        return requestService.getByRequesterById(userId, eventId);
    }

    @GetMapping("/attend/{userId}/event/{eventId}")
    public Boolean hasUserIdAttendEventId(@PathVariable Long userId,
                                          @PathVariable Long eventId) {
        return requestService.hasUserIdAttendEventId(userId, eventId);
    }
}
