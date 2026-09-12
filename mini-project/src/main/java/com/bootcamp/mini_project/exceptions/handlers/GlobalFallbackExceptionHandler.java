package com.bootcamp.mini_project.exceptions.handlers;

import com.bootcamp.mini_project.dto.common.ApiResponse;
import com.github.sebhoss.warnings.CompilerWarnings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Catches all unhandled system exceptions as a fallback handler.
 */
@RestControllerAdvice
@SuppressWarnings({CompilerWarnings.UNUSED})
public class GlobalFallbackExceptionHandler {
    /**
     * Handles all unhandled fallback system exceptions.
     *
     * @param exception the caught {@link Exception}
     * @return a generic 500 response entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGlobalException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message("An unexpected error occurred: " + exception.getMessage())
                        .data(null)
                        .build());
    }
}