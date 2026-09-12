package com.bootcamp.mini_project.dto.auth;

import lombok.*;

/**
 * Data transfer object for authentication responses containing JWT details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;

    private Long expiresIn;

    @Builder.Default
    private String tokenType = "Bearer";

    private String email;
}