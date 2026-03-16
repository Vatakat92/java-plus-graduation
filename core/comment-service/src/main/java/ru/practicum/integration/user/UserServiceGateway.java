package ru.practicum.integration.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ServiceUnavailableException;
import ru.practicum.integration.user.dto.UserShortDto;
import ru.practicum.integration.user.exceptions.UserNotFoundException;
import ru.practicum.integration.user.exceptions.UserServiceUnavailableException;

@Component
@RequiredArgsConstructor
public class UserServiceGateway {

    private final UserServiceClient userClient;

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