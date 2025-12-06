package com.repatroom.controller;

import com.repatroom.model.dto.ApiResponse;
import com.repatroom.model.dto.UserDTO;
import com.repatroom.security.JwtService;
import com.repatroom.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller
 * Handles user profile management and user-related operations
 * Requires authentication for all endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    /**
     * Get current user's profile
     * @param authorizationHeader JWT token from Authorization header
     * @return Current user's profile data
     */
    @Operation(summary = "Get current user profile",
            description = "Returns the profile of the currently authenticated user")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);

        log.info("Fetching profile for user ID: {}", userId);
        UserDTO userDTO = userService.getUserById(userId);

        return ResponseEntity.ok(ApiResponse.success(userDTO, "User profile retrieved successfully"));
    }

    /**
     * Update current user's profile
     * @param authorizationHeader JWT token from Authorization header
     * @param userDTO Updated user data
     * @return Updated user profile
     */
    @Operation(summary = "Update user profile",
            description = "Updates the profile of the currently authenticated user")
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UserDTO userDTO) {

        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);

        log.info("Updating profile for user ID: {}", userId);
        UserDTO updatedUser = userService.updateUserProfile(userId, userDTO);

        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Profile updated successfully"));
    }

    /**
     * Get user by ID (Admin only)
     * @param userId ID of the user to retrieve
     * @return User data
     */
    @Operation(summary = "Get user by ID",
            description = "Admin-only endpoint to get any user's profile by ID")
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String userId) {
        log.info("Admin fetching user by ID: {}", userId);
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(userDTO, "User retrieved successfully"));
    }

    /**
     * Get all users (Admin only)
     * @return List of all users
     */
    @Operation(summary = "Get all users",
            description = "Admin-only endpoint to get all registered users")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        log.info("Admin fetching all users");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
    }

    /**
     * Health check endpoint for user service
     * @return Simple status message
     */
    @Operation(summary = "User service health check")
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("User service is healthy", null));
    }
}