package ru.practicum.integration.request.exceptions;

public class RequestServiceUnavailableException extends RuntimeException {
    public RequestServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
