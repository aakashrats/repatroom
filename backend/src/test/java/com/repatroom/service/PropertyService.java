package com.repatroom.service;

import com.repatroom.model.dto.PropertyDTO;
import com.repatroom.model.entity.Property;
import com.repatroom.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Property business logic
 * Handles property search, creation, updates, and availability checks
 * Includes location-based search and filtering operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    /**
     * Search properties with multiple filters
     * Main search method used by customers to find properties
     * @param city the city to search in
     * @param type property type (PG, HOSTEL, FLAT, CO_LIVING)
     * @param category category (BOYS, GIRLS, FAMILY, BACHELOR)
     * @param minPrice minimum price per bed
     * @param maxPrice maximum price per bed
     * @param facilities list of required facilities
     * @return List of PropertyDTOs matching the criteria
     */
    public List<PropertyDTO> searchProperties(String city, String type, String category,
                                              Double minPrice, Double maxPrice, List<String> facilities) {
        log.info("Searching properties with filters - City: {}, Type: {}, Category: {}, Price: {}-{}, Facilities: {}",
                city, type, category, minPrice, maxPrice, facilities);

        List<Property> properties;

        if (city != null && type != null && category != null) {
            // Advanced search with all criteria
            properties = propertyRepository.findByAddressCityAndTypeAndCategoryAndIsActive(city, type, category, true);
        } else if (city != null) {
            // Basic search by city only
            properties = propertyRepository.findByCity(city);
        } else {
            // Get all active properties
            properties = propertyRepository.findByIsActive(true);
        }

        // Apply additional filters
        List<Property> filteredProperties = properties.stream()
                .filter(property -> filterByPrice(property, minPrice, maxPrice))
                .filter(property -> filterByFacilities(property, facilities))
                .collect(Collectors.toList());

        log.info("Found {} properties matching search criteria", filteredProperties.size());
        return filteredProperties.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get property by ID with full details
     * Used for property detail pages
     * @param propertyId the ID of the property to find
     * @return PropertyDTO with complete details
     */
    public PropertyDTO getPropertyById(String propertyId) {
        log.info("Fetching property by ID: {}", propertyId);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId));
        return convertToDTO(property);
    }

    /**
     * Get all properties for a specific owner
     * Used in owner dashboard
     * @param ownerId the ID of the property owner
     * @return List of PropertyDTOs belonging to the owner
     */
    public List<PropertyDTO> getPropertiesByOwner(String ownerId) {
        log.info("Fetching properties for owner: {}", ownerId);
        List<Property> properties = propertyRepository.findByOwnerIdAndIsActive(ownerId, true);
        return properties.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create new property
     * Used when owners add new properties to the platform
     * @param propertyDTO the property data to create
     * @param ownerId the ID of the property owner
     * @return Created PropertyDTO
     */
    public PropertyDTO createProperty(PropertyDTO propertyDTO, String ownerId) {
        log.info("Creating new property for owner: {}", ownerId);

        Property property = new Property();
        property.setOwnerId(ownerId);
        property.setName(propertyDTO.getName());
        property.setDescription(propertyDTO.getDescription());
        property.setType(propertyDTO.getType());
        property.setCategory(propertyDTO.getCategory());

        // Set address with geolocation
        Property.Address address = new Property.Address();
        address.setStreet(propertyDTO.getAddress().getStreet());
        address.setLandmark(propertyDTO.getAddress().getLandmark());
        address.setArea(propertyDTO.getAddress().getArea());
        address.setCity(propertyDTO.getAddress().getCity());
        address.setState(propertyDTO.getAddress().getState());
        address.setPincode(propertyDTO.getAddress().getPincode());

        // Convert latitude/longitude to GeoJsonPoint
        if (propertyDTO.getAddress().getLatitude() != null && propertyDTO.getAddress().getLongitude() != null) {
            Point point = new Point(propertyDTO.getAddress().getLongitude(), propertyDTO.getAddress().getLatitude());
            address.setLocation(new GeoJsonPoint(point));
        }

        property.setAddress(address);
        property.setFacilities(propertyDTO.getFacilities());
        property.setAmenities(propertyDTO.getAmenities());

        Property savedProperty = propertyRepository.save(property);
        log.info("Property created successfully with ID: {}", savedProperty.getId());
        return convertToDTO(savedProperty);
    }

    /**
     * Filter properties by price range
     * Checks if any room in the property falls within the price range
     */
    private boolean filterByPrice(Property property, Double minPrice, Double maxPrice) {
        if (minPrice == null && maxPrice == null) return true;

        return property.getRooms().stream()
                .anyMatch(room -> {
                    boolean matchesMin = minPrice == null || room.getPricePerBed() >= minPrice;
                    boolean matchesMax = maxPrice == null || room.getPricePerBed() <= maxPrice;
                    return matchesMin && matchesMax && room.isActive();
                });
    }

    /**
     * Filter properties by facilities
     * Checks if property has all required facilities
     */
    private boolean filterByFacilities(Property property, List<String> facilities) {
        if (facilities == null || facilities.isEmpty()) return true;

        return property.getFacilities().containsAll(facilities);
    }

    /**
     * Convert Property entity to PropertyDTO
     * Calculates additional fields for frontend
     */
    private PropertyDTO convertToDTO(Property property) {
        // Calculate total available beds
        int availableBeds = property.getRooms().stream()
                .filter(Property.Room::isActive)
                .mapToInt(Property.Room::getAvailableBeds)
                .sum();

        return PropertyDTO.builder()
                .id(property.getId())
                .ownerId(property.getOwnerId())
                .name(property.getName())
                .description(property.getDescription())
                .type(property.getType())
                .category(property.getCategory())
                .address(convertAddressToDTO(property.getAddress()))
                .contact(convertContactToDTO(property.getContact()))
                .facilities(property.getFacilities())
                .amenities(property.getAmenities())
                .rooms(property.getRooms().stream()
                        .map(this::convertRoomToDTO)
                        .collect(Collectors.toList()))
                .images(property.getImages())
                .isActive(property.isActive())
                .isVerified(property.isVerified())
                .rating(property.getRating())
                .totalReviews(property.getTotalReviews())
                .createdAt(property.getCreatedAt() != null ? property.getCreatedAt().toString() : null)
                .availableBeds(availableBeds)
                .build();
    }

    private PropertyDTO.AddressDTO convertAddressToDTO(Property.Address address) {
        if (address == null) return null;

        return PropertyDTO.AddressDTO.builder()
                .street(address.getStreet())
                .landmark(address.getLandmark())
                .area(address.getArea())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .latitude(address.getLocation() != null ? address.getLocation().getY() : null)
                .longitude(address.getLocation() != null ? address.getLocation().getX() : null)
                .build();
    }

    private PropertyDTO.ContactDTO convertContactToDTO(Property.Contact contact) {
        if (contact == null) return null;

        return PropertyDTO.ContactDTO.builder()
                .primaryPhone(contact.getPrimaryPhone())
                .secondaryPhone(contact.getSecondaryPhone())
                .email(contact.getEmail())
                .build();
    }

    private PropertyDTO.RoomDTO convertRoomToDTO(Property.Room room) {
        return PropertyDTO.RoomDTO.builder()
                .roomId(room.getRoomId())
                .type(room.getType())
                .availableBeds(room.getAvailableBeds())
                .totalBeds(room.getTotalBeds())
                .pricePerBed(room.getPricePerBed())
                .amenities(room.getAmenities())
                .images(room.getImages())
                .isActive(room.isActive())
                .build();
    }
}