package com.bootcamp.mini_project.exceptions.handlers;

import com.bootcamp.mini_project.dto.common.ApiResponse;
import com.github.sebhoss.warnings.CompilerWarnings;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * Intercepts and processes request validation errors.
 */
@RestControllerAdvice
@SuppressWarnings(CompilerWarnings.UNUSED)
public class ValidationExceptionHandler {
    /**
     * Handles path variable and request parameter validation constraints.
     *
     * @param exception the caught {@link ConstraintViolationException}
     * @return a response entity containing the constraint violation message
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleConstraintViolationException(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Validation failed.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message(message)
                        .data(null)
                        .build());
    }

    /**
     * Handles DTO validation errors triggered by {@code @Valid} annotations.
     *
     * @param exception the caught {@link MethodArgumentNotValidException}
     * @return a response entity containing the first validation error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid input.")
                .findFirst()
                .orElse("Validation failed.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message(message)
                        .build());
    }
}