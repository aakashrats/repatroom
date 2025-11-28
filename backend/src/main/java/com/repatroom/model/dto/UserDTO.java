package com.repatroom.model.dto;


import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for User entity
 * Used to send user data to frontend without exposing sensitive information
 * Excludes password and other internal fields
 */
@Data
@Builder
public class UserDTO {

    private String id;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String phone;
    private String avatar;
    private boolean isActive;
    private boolean emailVerified;
    private String createdAt;
    private String updatedAt;
}
