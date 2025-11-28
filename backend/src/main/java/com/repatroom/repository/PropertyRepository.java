package com.repatroom.repository;

import com.repatroom.model.entity.Property;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Property entity with advanced search operations
 * Includes geospatial queries for location-based search
 */
@Repository
public interface PropertyRepository extends MongoRepository<Property, String> {

    /**
     * Find all properties by owner ID
     * Used in owner dashboard to show their properties
     * @param ownerId the ID of the property owner
     * @return List of properties belonging to the owner
     */
    List<Property> findByOwnerId(String ownerId);

    /**
     * Find properties by city - basic location search
     * @param city city name to search in
     * @return List of properties in the specified city
     */
    @Query("{ 'address.city': ?0 }")
    List<Property> findByCity(String city);

    /**
     * Find properties by type (PG, HOSTEL, FLAT, CO_LIVING)
     * @param type the property type
     * @return List of properties of the specified type
     */
    List<Property> findByType(String type);

    /**
     * Find properties by category (BOYS, GIRLS, FAMILY, BACHELOR)
     * @param category the property category
     * @return List of properties in the specified category
     */
    List<Property> findByCategory(String category);

    /**
     * Find active properties only (not deleted or disabled)
     * @param isActive true for active properties
     * @return List of active properties
     */
    List<Property> findByIsActive(boolean isActive);

    /**
     * Find properties with specific facilities
     * Uses MongoDB's $in operator to check if any facility matches
     * @param facilities list of facilities to search for
     * @return List of properties that have at least one of the specified facilities
     */
    @Query("{ 'facilities': { $in: ?0 } }")
    List<Property> findByFacilitiesIn(List<String> facilities);

    /**
     * Find properties within a price range
     * Searches in room prices for available beds
     * @param minPrice minimum price per bed
     * @param maxPrice maximum price per bed
     * @return List of properties within the price range
     */
    @Query("{ 'rooms.pricePerBed': { $gte: ?0, $lte: ?1 } }")
    List<Property> findByPriceRange(double minPrice, double maxPrice);

    /**
     * Advanced search with multiple criteria
     * Combines city, type, category, and active status
     * @param city the city to search in
     * @param type the property type
     * @param category the property category
     * @param isActive active status
     * @return List of properties matching all criteria
     */
    List<Property> findByAddressCityAndTypeAndCategoryAndIsActive(
            String city, String type, String category, boolean isActive);

    /**
     * Find properties by owner ID and active status
     * Useful for owners to see only their active properties
     * @param ownerId the owner's ID
     * @param isActive active status
     * @return List of owner's properties with specified active status
     */
    List<Property> findByOwnerIdAndIsActive(String ownerId, boolean isActive);
}