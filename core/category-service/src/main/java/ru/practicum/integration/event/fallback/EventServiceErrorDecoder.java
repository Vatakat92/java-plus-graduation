package ru.practicum.integration.event.fallback;

import feign.Response;
import feign.codec.ErrorDecoder;

public class EventServiceErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return defaultDecoder.decode(methodKey, response);
    }
}