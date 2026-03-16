package ru.practicum.integration.request.fallback;

import feign.Response;
import feign.codec.ErrorDecoder;
import ru.practicum.integration.request.exceptions.RequestNotFoundException;

public class RequestServiceErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            return new RequestNotFoundException("Requester was not found");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}