package com.repatroom.model.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * Standard API response wrapper for all API endpoints
 * Provides consistent response structure across the application
 * Includes success status, message, data, and timestamp
 */
@Data
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String timestamp;

    /**
     * Create successful response with data
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    /**
     * Create successful response without data
     */
    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    /**
     * Create error response
     */
    public static ApiResponse<Void> error(String message) {
        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}