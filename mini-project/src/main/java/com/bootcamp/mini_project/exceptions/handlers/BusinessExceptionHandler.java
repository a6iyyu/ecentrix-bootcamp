package com.bootcamp.mini_project.exceptions.handlers;

import com.bootcamp.mini_project.dto.common.ApiResponse;
import com.bootcamp.mini_project.exceptions.custom.ResourceNotFoundException;
import com.github.sebhoss.warnings.CompilerWarnings;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Intercepts business logic exceptions and explicit status responses.
 */
@RestControllerAdvice
@SuppressWarnings(CompilerWarnings.UNUSED)
public class BusinessExceptionHandler {
    /**
     * Handles custom API status exceptions thrown directly from business logic.
     *
     * @param exception the caught {@link ResponseStatusException}
     * @return a response entity matching the exception status code and reason
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<String>> handleApiException(ResponseStatusException exception) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message(exception.getReason())
                        .build());
    }

    /**
     * Handles missing resource exceptions and returns a 404 NOT FOUND status.
     *
     * @param exception the caught {@link ResourceNotFoundException}
     * @return a response entity with HTTP 404 status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message(exception.getMessage())
                        .build());
    }
}