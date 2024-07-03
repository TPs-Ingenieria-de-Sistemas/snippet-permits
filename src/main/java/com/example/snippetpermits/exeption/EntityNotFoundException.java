package com.example.snippetpermits.exeption;

public class EntityNotFoundException extends  RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
