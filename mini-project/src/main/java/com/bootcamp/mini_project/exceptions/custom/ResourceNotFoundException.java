package com.bootcamp.mini_project.exceptions.custom;

/**
 * Signals that a requested database entity or resource could not be found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}