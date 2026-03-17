package ru.practicum.integration.stats.fallback;

import feign.Response;
import feign.codec.ErrorDecoder;
import ru.practicum.integration.stats.exceptions.StatsNotFoundException;

public class StatsServiceErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            return new StatsNotFoundException("Requester was not found");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}