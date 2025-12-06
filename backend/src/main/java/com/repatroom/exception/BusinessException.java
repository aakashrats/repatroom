package com.repatroom.exception;

/**
 * Exception for business logic violations
 * e.g., Booking conflict, Invalid operation, Business rule violation
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}