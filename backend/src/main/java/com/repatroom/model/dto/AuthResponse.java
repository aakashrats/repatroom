package com.repatroom.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for authentication responses
 * Sent back to frontend after successful login/register
 * Contains JWT token and user information
 */
@Data
@Builder
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private UserDTO user;
    private String message;

    public static class UserDTO {
        private String id;
        private String email;
        private String role;
        private String firstName;
        private String lastName;
        private String phone;
    }
}