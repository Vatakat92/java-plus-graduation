package ru.practicum.integration.request.exceptions;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(String m){
        super(m);
    }
}
