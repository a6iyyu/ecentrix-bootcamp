package com.bootcamp.mini_project.dto.common;

import lombok.*;

/**
 * Standardizes API response structures across all HTTP endpoints.
 *
 * @param <T> the data payload type.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    /**
     * Indicates whether the API request was successful.
     * <p>Defaults to {@code true}.</p>
     */
    @Builder.Default
    private boolean success = true;

    /**
     * A descriptive message providing details about the response status or any errors.
     */
    private String message;

    /**
     * The actual data payload returned by the API endpoint.
     */
    private T data;
}