package com.repatroom.repository;

import com.repatroom.model.entity.Booking;
import com.repatroom.model.enums.BookingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Booking entity with booking-specific queries
 * Includes date range queries and status-based searches
 */
@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    /**
     * Find all bookings for a specific customer
     * Used in customer dashboard to show their booking history
     * @param customerId the customer's ID
     * @return List of customer's bookings
     */
    List<Booking> findByCustomerId(String customerId);

    /**
     * Find all bookings for a specific property
     * Used by owners to manage bookings for their property
     * @param propertyId the property's ID
     * @return List of bookings for the property
     */
    List<Booking> findByPropertyId(String propertyId);

    /**
     * Find bookings by status (PENDING, CONFIRMED, CANCELLED, etc.)
     * Useful for filtering bookings by their current state
     * @param status the booking status
     * @return List of bookings with the specified status
     */
    List<Booking> findByStatus(BookingStatus status);

    /**
     * Find bookings by customer and status
     * Example: Find all confirmed bookings for a customer
     * @param customerId the customer's ID
     * @param status the booking status
     * @return List of customer's bookings with specified status
     */
    List<Booking> findByCustomerIdAndStatus(String customerId, BookingStatus status);

    /**
     * Find booking by unique booking ID (BR00123456 format)
     * Used for booking confirmation and tracking
     * @param bookingId the custom booking ID
     * @return Optional containing booking if found
     */
    Optional<Booking> findByBookingId(String bookingId);

    /**
     * Check for date conflicts - find overlapping bookings for a room
     * Important for availability checking
     * @param propertyId the property ID
     * @param roomId the room ID
     * @param checkInDate check-in date to verify
     * @param checkOutDate check-out date to verify
     * @param status booking status to consider (usually CONFIRMED)
     * @return List of conflicting bookings
     */
    @Query("{ " +
            "'propertyId': ?0, " +
            "'roomId': ?1, " +
            "'status': ?4, " +
            "'$or': [ " +
            "   { 'checkInDate': { '$lt': ?3, '$gte': ?2 } }, " +
            "   { 'checkOutDate': { '$gt': ?2, '$lte': ?3 } }, " +
            "   { 'checkInDate': { '$gte': ?2 }, 'checkOutDate': { '$lte': ?3 } } " +
            "]" +
            "}")
    List<Booking> findConflictingBookings(
            String propertyId,
            String roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            BookingStatus status);

    /**
     * Find bookings that need automatic status updates
     * Example: Find confirmed bookings where check-in date has passed
     * @param currentDate the current date to compare with
     * @param fromStatus the current status to look for
     * @param toStatus the status to potentially update to
     * @return List of bookings that need status update
     */
    @Query("{ " +
            "'checkInDate': { $lte: ?0 }, " +
            "'status': ?1 " +
            "}")
    List<Booking> findBookingsForStatusUpdate(
            LocalDate currentDate,
            BookingStatus fromStatus,
            BookingStatus toStatus);

    /**
     * Count bookings by status for a property
     * Useful for analytics and dashboard statistics
     * @param propertyId the property ID
     * @param status the booking status to count
     * @return count of bookings with specified status
     */
    long countByPropertyIdAndStatus(String propertyId, BookingStatus status);
}