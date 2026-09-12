package com.bootcamp.mini_project.exceptions.handlers;

import com.bootcamp.mini_project.dto.common.ApiResponse;
import com.bootcamp.mini_project.exceptions.custom.*;
import com.github.sebhoss.warnings.CompilerWarnings;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * Intercepts infrastructure errors such as Redis caching and Kafka messaging failures.
 */
@RestControllerAdvice
@SuppressWarnings(CompilerWarnings.UNUSED)
public class InfrastructureExceptionHandler {
    /**
     * Handles exceptions originating from Kafka messaging operations.
     *
     * @param exception the caught {@link KafkaOperationException}
     * @return a response entity with internal server error status
     */
    @ExceptionHandler(KafkaOperationException.class)
    public ResponseEntity<ApiResponse<String>> handleKafkaException(KafkaOperationException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }

    /**
     * Handles exceptions originating from Redis caching operations.
     *
     * @param exception the caught {@link RedisCacheException}
     * @return a response entity with internal server error status
     */
    @ExceptionHandler(RedisCacheException.class)
    public ResponseEntity<ApiResponse<String>> handleRedisException(RedisCacheException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }
}