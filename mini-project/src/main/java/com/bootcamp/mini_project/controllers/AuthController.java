package com.bootcamp.mini_project.controllers;

import com.bootcamp.mini_project.dto.auth.*;
import com.bootcamp.mini_project.dto.common.ApiResponse;
import com.bootcamp.mini_project.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Controller providing REST endpoints for user authentication and session termination.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user login and token management")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials and issue JWT access token")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("[API REST] Login request received for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("User authenticated successfully")
                .data(response)
                .build();
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalidate active JWT token via Redis blacklist")
    public ApiResponse<AuthResponse> logout(@RequestHeader(name = "Authorization", required = false) String token) {
        log.info("[API REST] Logout request received");
        authService.logout(token);

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("User logged out successfully")
                .data(null)
                .build();
    }
}