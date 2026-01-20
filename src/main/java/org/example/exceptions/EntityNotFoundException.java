package org.example.exceptions;

// Extender RuntimeException es crucial
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}