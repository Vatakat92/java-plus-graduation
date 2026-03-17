package ru.practicum.exceptions;

public class TitleExistsException extends RuntimeException {
    public TitleExistsException(String m) {
        super(m);
    }
}