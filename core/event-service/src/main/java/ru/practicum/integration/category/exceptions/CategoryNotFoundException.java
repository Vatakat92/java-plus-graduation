package ru.practicum.integration.category.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String m){
        super(m);
    }
}
