package com.repatroom.controller;

import com.repatroom.model.dto.ApiResponse;
import com.repatroom.model.dto.AuthRequest;
import com.repatroom.model.dto.AuthResponse;
import com.repatroom.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles user registration and login endpoints
 * Public endpoints - no authentication required
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     * @param authRequest contains email, password, and optional role
     * @return AuthResponse with JWT token and user data
     */
    @Operation(summary = "Register a new user",
            description = "Creates a new user account. Role defaults to CUSTOMER if not specified.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Register request for email: {}", authRequest.getEmail());

        try {
            AuthResponse response = authService.register(authRequest);
            return ResponseEntity.ok(ApiResponse.success(response, "User registered successfully"));
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Login user
     * @param authRequest contains email and password
     * @return AuthResponse with JWT token and user data
     */
    @Operation(summary = "Login user",
            description = "Authenticates user and returns JWT token")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Login request for email: {}", authRequest.getEmail());

        try {
            AuthResponse response = authService.login(authRequest);
            return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Health check endpoint for authentication service
     * @return Simple status message
     */
    @Operation(summary = "Auth health check",
            description = "Check if authentication service is running")
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Auth service is healthy", null));
    }
}