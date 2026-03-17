package ru.practicum.integration.user.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String m){
        super(m);
    }
}
