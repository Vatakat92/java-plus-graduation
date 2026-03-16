package ru.practicum.controller.internal;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.internal.UserIdsBatchRequest;
import ru.practicum.dto.internal.UserShortDto;
import ru.practicum.service.UserService;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/by-id/{userId}")
    public UserShortDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/by-ids")
    public Map<Long, UserShortDto> getUserByIds(@RequestBody UserIdsBatchRequest userIds) {
        return userService.getUsersByIds(userIds.ids());
    }
}
