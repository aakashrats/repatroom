package com.repatroom.repository;

import com.repatroom.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 * Extends MongoRepository which provides basic CRUD operations
 * No need to implement these methods - Spring Data MongoDB does it automatically
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find user by email (unique field)
     * Spring Data automatically implements this method based on method name
     * @param email user's email address
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email
     * Useful for validation during registration
     * @param email email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find all users by their role (CUSTOMER, OWNER, ADMIN)
     * @param role the user role to search for
     * @return List of users with the specified role
     */
    List<User> findByRole(String role);

    /**
     * Find users by city in their address (if we add address to user later)
     * Custom query using MongoDB JSON query syntax
     * @param city city to search for
     * @return List of users in the specified city
     */
    @Query("{ 'profile.address.city': ?0 }")
    List<User> findByCity(String city);

    /**
     * Find active users only
     * @param isActive true for active users, false for inactive
     * @return List of users based on active status
     */
    List<User> findByIsActive(boolean isActive);
}