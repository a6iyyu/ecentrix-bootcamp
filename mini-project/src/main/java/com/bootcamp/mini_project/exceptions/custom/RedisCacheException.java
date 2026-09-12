package com.bootcamp.mini_project.exceptions.custom;

/**
 * Signals failures occurring during Redis caching operations.
 */
public class RedisCacheException extends RuntimeException {
    public RedisCacheException(String message) {
        super(message);
    }
}