package com.repatroom.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object for authentication requests (login/register)
 * Used to receive user credentials from frontend
 * Includes validation annotations for automatic input validation
 */
@Data
public class AuthRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid emial address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    // Optional: Role for registration (defaults to CUSTOMER)
    private String role;
}
