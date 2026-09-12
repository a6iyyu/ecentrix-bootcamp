package com.bootcamp.mini_project.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Data transfer object for user login requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "Email is required.")
    @Email(message = "Email address must be valid.")
    @Size(max = 255, message = "Email must not exceed 255 characters.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;
}