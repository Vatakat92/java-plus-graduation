package ru.practicum.integration.user;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ServiceUnavailableException;
import ru.practicum.integration.user.dto.UserIdsBatchRequest;
import ru.practicum.integration.user.dto.UserShortDto;
import ru.practicum.integration.user.exceptions.UserNotFoundException;
import ru.practicum.integration.user.exceptions.UserServiceUnavailableException;

@Component
@RequiredArgsConstructor
public class UserServiceGateway {

    private final UserServiceClient userClient;

    public Map<Long, UserShortDto> getUserByIds(List<Long> userIds) {

        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        try {
            return userClient.getUsersByIds(new UserIdsBatchRequest(userIds));
        } catch (UserServiceUnavailableException e) {
            throw new ServiceUnavailableException("User service is temporarily unavailable");
        }
    }

    public UserShortDto getUserById(Long userId) {
        try {
            return userClient.getUserById(userId);
        } catch (UserNotFoundException e) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        } catch (UserServiceUnavailableException e) {
            throw new ServiceUnavailableException("User service is temporarily unavailable");
        }
    }

}