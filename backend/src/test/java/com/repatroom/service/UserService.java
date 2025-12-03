package com.repatroom.service;

import com.repatroom.model.dto.UserDTO;
import com.repatroom.model.entity.User;
import com.repatroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for User business logic
 * Contains methods for user management, profile updates, and user-related operations
 * Uses Repository for data access and returns DTOs to controllers
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Get user by ID and convert to DTO
     * Used when we need to return user data to frontend
     * @param userId the ID of the user to find
     * @return UserDTO containing user information
     * @throws RuntimeException if user not found
     */
    public UserDTO getUserById(String userId) {
        log.info("Fetching user by ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return convertToDTO(user);
    }

    /**
     * Get user by email
     * Used during authentication and profile lookups
     * @param email the email to search for
     * @return User entity (internal use, contains password)
     */
    public User getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Get all users (for admin purposes)
     * @return List of UserDTOs
     */
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update user profile information
     * Allows users to update their personal details
     * @param userId the ID of the user to update
     * @param userDTO the updated user data
     * @return Updated UserDTO
     */
    public UserDTO updateUserProfile(String userId, UserDTO userDTO) {
        log.info("Updating profile for user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Update profile fields
        if (userDTO.getFirstName() != null) {
            user.getProfile().setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.getProfile().setLastName(userDTO.getLastName());
        }
        if (userDTO.getPhone() != null) {
            user.getProfile().setPhone(userDTO.getPhone());
        }
        if (userDTO.getAvatar() != null) {
            user.getProfile().setAvatar(userDTO.getAvatar());
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", userId);
        return convertToDTO(updatedUser);
    }

    /**
     * Check if email already exists in database
     * Used during registration to prevent duplicate accounts
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Convert User entity to UserDTO
     * Removes sensitive information like password
     * @param user the User entity to convert
     * @return UserDTO without sensitive data
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .firstName(user.getProfile().getFirstName())
                .lastName(user.getProfile().getLastName())
                .phone(user.getProfile().getPhone())
                .avatar(user.getProfile().getAvatar())
                .isActive(user.isActive())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                .build();
    }
}