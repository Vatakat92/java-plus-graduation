package ru.practicum.integration.user.fallback;

import feign.Response;
import feign.codec.ErrorDecoder;
import ru.practicum.integration.user.exceptions.UserNotFoundException;

public class UserServiceErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            return new UserNotFoundException("User was not found");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}