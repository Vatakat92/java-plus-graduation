package ru.practicum.integration.stats.exceptions;

public class StatsServiceUnavailableException extends RuntimeException {
    public StatsServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
