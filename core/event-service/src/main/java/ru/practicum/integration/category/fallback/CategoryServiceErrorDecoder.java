package ru.practicum.integration.category.fallback;

import feign.Response;
import feign.codec.ErrorDecoder;
import ru.practicum.integration.category.exceptions.CategoryNotFoundException;

public class CategoryServiceErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            return new CategoryNotFoundException("Event was not found");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}