package com.repatroom.service;

import com.repatroom.model.dto.AuthRequest;
import com.repatroom.model.dto.AuthResponse;
import com.repatroom.model.entity.User;
import com.repatroom.model.enums.UserRole;
import com.repatroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service class for Authentication business logic
 * Handles user registration, login, and token management
 * Works with JWT service for token generation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * Register a new user
     * Creates user account with hashed password and default role
     * @param authRequest contains email, password, and optional role
     * @return AuthResponse with JWT token and user data
     * @throws RuntimeException if email already exists
     */
    public AuthResponse register(AuthRequest authRequest) {
        log.info("Attempting to register user with email: {}", authRequest.getEmail());

        // Check if email already exists
        if (userService.isEmailExists(authRequest.getEmail())) {
            throw new RuntimeException("Email already registered: " + authRequest.getEmail());
        }

        // Create new user
        User user = new User();
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));

        // Set role (default to CUSTOMER if not provided)
        UserRole role = authRequest.getRole() != null ?
                UserRole.valueOf(authRequest.getRole().toUpperCase()) : UserRole.CUSTOMER;
        user.setRole(role);

        // Initialize profile
        User.Profile profile = new User.Profile();
        profile.setFirstName(""); // Will be updated later
        profile.setLastName("");
        profile.setPhone("");
        user.setProfile(profile);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Generate JWT token
        String token = jwtService.generateToken(savedUser);

        return buildAuthResponse(token, savedUser, "Registration successful");
    }

    /**
     * Authenticate user login
     * Verifies credentials and generates JWT token
     * @param authRequest contains email and password
     * @return AuthResponse with JWT token and user data
     * @throws RuntimeException if credentials are invalid
     */
    public AuthResponse login(AuthRequest authRequest) {
        log.info("Attempting login for user: {}", authRequest.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Check if user is active
        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user);
        log.info("Login successful for user: {}", authRequest.getEmail());

        return buildAuthResponse(token, user, "Login successful");
    }

    /**
     * Build authentication response with user data and token
     */
    private AuthResponse buildAuthResponse(String token, User user, String message) {
        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .firstName(user.getProfile().getFirstName())
                        .lastName(user.getProfile().getLastName())
                        .phone(user.getProfile().getPhone())
                        .build())
                .message(message)
                .build();
    }
}