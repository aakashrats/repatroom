package com.repatroom.controller;

import com.repatroom.model.dto.ApiResponse;
import com.repatroom.model.dto.PropertyDTO;
import com.repatroom.security.JwtService;
import com.repatroom.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Property Controller
 * Handles property search, listing, and management
 * Mix of public and authenticated endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Tag(name = "Properties", description = "Property search and management endpoints")
public class PropertyController {

    private final PropertyService propertyService;
    private final JwtService jwtService;

    /**
     * Search properties with filters (Public)
     * @param city Filter by city
     * @param type Filter by property type (PG, HOSTEL, FLAT, CO_LIVING)
     * @param category Filter by category (BOYS, GIRLS, FAMILY, BACHELOR)
     * @param minPrice Minimum price per bed
     * @param maxPrice Maximum price per bed
     * @param facilities Comma-separated list of required facilities
     * @return List of properties matching criteria
     */
    @Operation(summary = "Search properties",
            description = "Public endpoint to search properties with multiple filters")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PropertyDTO>>> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<String> facilities) {

        log.info("Searching properties - City: {}, Type: {}, Category: {}", city, type, category);

        List<PropertyDTO> properties = propertyService.searchProperties(
                city, type, category, minPrice, maxPrice, facilities);

        return ResponseEntity.ok(
                ApiResponse.success(properties, "Properties retrieved successfully"));
    }

    /**
     * Get property by ID (Public)
     * @param propertyId ID of the property to retrieve
     * @return Property details
     */
    @Operation(summary = "Get property details",
            description = "Public endpoint to get detailed information about a property")
    @GetMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<PropertyDTO>> getPropertyById(@PathVariable String propertyId) {
        log.info("Fetching property by ID: {}", propertyId);
        PropertyDTO property = propertyService.getPropertyById(propertyId);
        return ResponseEntity.ok(
                ApiResponse.success(property, "Property retrieved successfully"));
    }

    /**
     * Get properties for current owner (Owner only)
     * @param authorizationHeader JWT token
     * @return List of owner's properties
     */
    @Operation(summary = "Get owner's properties",
            description = "Returns all properties belonging to the authenticated owner")
    @GetMapping("/owner/my-properties")
    @PreAuthorize("hasRole('OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<PropertyDTO>>> getOwnerProperties(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        String ownerId = jwtService.extractUserId(token);

        log.info("Fetching properties for owner: {}", ownerId);
        List<PropertyDTO> properties = propertyService.getPropertiesByOwner(ownerId);

        return ResponseEntity.ok(
                ApiResponse.success(properties, "Owner properties retrieved successfully"));
    }

    /**
     * Create new property (Owner only)
     * @param authorizationHeader JWT token
     * @param propertyDTO Property data to create
     * @return Created property
     */
    @Operation(summary = "Create new property",
            description = "Owner-only endpoint to add a new property")
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PropertyDTO>> createProperty(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody PropertyDTO propertyDTO) {

        String token = authorizationHeader.substring(7);
        String ownerId = jwtService.extractUserId(token);

        log.info("Creating new property for owner: {}", ownerId);
        PropertyDTO createdProperty = propertyService.createProperty(propertyDTO, ownerId);

        return ResponseEntity.ok(
                ApiResponse.success(createdProperty, "Property created successfully"));
    }

    /**
     * Health check endpoint
     * @return Simple status message
     */
    @Operation(summary = "Property service health check")
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Property service is healthy", null));
    }
}