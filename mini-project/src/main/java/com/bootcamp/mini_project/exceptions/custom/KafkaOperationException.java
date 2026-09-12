package com.bootcamp.mini_project.exceptions.custom;

/**
 * Signals failures occurring during Kafka event publishing or consuming operations.
 */
public class KafkaOperationException extends RuntimeException {
    public KafkaOperationException(String message) {
        super(message);
    }
}