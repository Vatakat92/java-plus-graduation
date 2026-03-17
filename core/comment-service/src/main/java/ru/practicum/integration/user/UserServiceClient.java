package ru.practicum.integration.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.integration.user.config.UserServiceClientConfig;
import ru.practicum.integration.user.dto.UserShortDto;
import ru.practicum.integration.user.fallback.UserServiceClientFallbackFactory;

@FeignClient(
    name = "user-service",
    path = "/internal/users",
    configuration = UserServiceClientConfig.class,
    fallbackFactory = UserServiceClientFallbackFactory.class
)

public interface UserServiceClient {
    @GetMapping("/by-id/{userId}")
    UserShortDto getUserById(@PathVariable Long userId);
}
