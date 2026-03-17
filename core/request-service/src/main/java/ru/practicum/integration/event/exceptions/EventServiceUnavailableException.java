package ru.practicum.integration.event.exceptions;

public class EventServiceUnavailableException extends RuntimeException {
    public EventServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
