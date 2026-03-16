package ru.practicum.integration.category.exceptions;

public class CategoryServiceUnavailableException extends RuntimeException {
    public CategoryServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
