package com.bootcamp.mini_project.services;

import com.bootcamp.mini_project.dto.auth.*;
import com.bootcamp.mini_project.entity.User;
import com.bootcamp.mini_project.exceptions.custom.ResourceNotFoundException;
import com.bootcamp.mini_project.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Authenticates a user with provided credentials and generates an access token.
     *
     * @param request payload containing user login credentials
     * @return {@link AuthResponse} containing authentication token and user profile
     * @throws ResourceNotFoundException if user email is not registered
     * @throws IllegalArgumentException  if password verification fails
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("[AUTH] Attempting login for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> {
            log.warn("[AUTH FAILED] Login failed. Email '{}' not found.", request.getEmail());
            return new ResourceNotFoundException("User not found with email: " + request.getEmail());
        });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("[AUTH FAILED] Login failed. Invalid password for email: {}", request.getEmail());
            throw new IllegalArgumentException("Invalid email or password.");
        }

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpirationMs);

        String generatedToken = Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();

        log.info("[AUTH SUCCESS] User ID {} logged in successfully.", user.getId());

        return AuthResponse.builder()
                .accessToken(generatedToken)
                .expiresIn(jwtExpirationMs)
                .tokenType("Bearer")
                .email(user.getEmail())
                .build();
    }

    /**
     * Invalidates the user's active token by storing it in the Redis blacklist.
     *
     * @param token the authorization header or bearer token to invalidate
     */
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            log.warn("[AUTH LOGOUT] Logout failed. Provided token is empty.");
            return;
        }

        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        redisTemplate.opsForValue().set("blacklist:" + jwtToken, "true", Duration.ofMillis(jwtExpirationMs));
        log.info("[AUTH LOGOUT] Token successfully blacklisted in Redis.");
    }
}