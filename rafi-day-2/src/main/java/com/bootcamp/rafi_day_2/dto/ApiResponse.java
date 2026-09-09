package com.bootcamp.rafi_day_2.dto;

import lombok.*;

/**
 * A generic wrapper class for consistent API responses.
 * It standardizes the structure of data sent back to the client.
 *
 * @param <T> The type of the data payload contained in the response.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    @Builder.Default
    private boolean success = true;
    private String message;
    private T data;
}