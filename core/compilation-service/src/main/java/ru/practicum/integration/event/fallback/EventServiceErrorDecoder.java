package ru.practicum.integration.event.fallback;

import feign.Response;
import feign.codec.ErrorDecoder;
import ru.practicum.integration.event.exceptions.EventNotFoundException;

public class EventServiceErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            return new EventNotFoundException("Event was not found");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}