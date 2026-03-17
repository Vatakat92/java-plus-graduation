package ru.practicum.integration.user.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.integration.user.UserServiceClient;
import ru.practicum.integration.user.exceptions.UserServiceUnavailableException;

@Component
@Slf4j
public class UserServiceClientFallbackFactory implements FallbackFactory<UserServiceClient> {

    @Override
    public UserServiceClient create(Throwable cause) {
        return userId -> {
            log.warn("User service is unavailable. userId={}, cause={}", userId, cause.toString());
            throw new UserServiceUnavailableException("User service is unavailable.", cause);
        };
    }
}