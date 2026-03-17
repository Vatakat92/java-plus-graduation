package ru.practicum.integration.event.exceptions;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String m){
        super(m);
    }
}
