package ru.practicum.exceptions;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String m) {
        super(m);
    }
}
