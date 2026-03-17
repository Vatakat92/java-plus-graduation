package ru.practicum.integration.user;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.integration.user.config.UserServiceClientConfig;
import ru.practicum.integration.user.dto.UserIdsBatchRequest;
import ru.practicum.integration.user.dto.UserShortDto;
import ru.practicum.integration.user.fallback.UserServiceClientFallbackFactory;

@FeignClient(
    name = "user-service",
    path = "/internal/users",
    configuration = UserServiceClientConfig.class,
    fallbackFactory = UserServiceClientFallbackFactory.class
)

public interface UserServiceClient {
    @PostMapping("/by-ids")
    Map<Long, UserShortDto> getUsersByIds(@RequestBody UserIdsBatchRequest userIds);

    @GetMapping("/by-id/{userId}")
    UserShortDto getUserById(@PathVariable Long userId);
}
