package com.repatroom.model.dto;


import lombok.Data;
import lombok.Builder;
import java.util.List;

/**
 * Data Transfer Object for Property entity
 * Used for property listing and details in API responses
 * Can include calculated fields like distance, etc.
 */
@Data
@Builder
public class PropertyDTO {

    private String id;
    private String ownerId;
    private String name;
    private String description;
    private String type;
    private String category;


    private AddressDTO address;
    private ContactDTO contact;
    private List<String> facilities;
    private List<String> amenities;
    private List<RoomDTO> rooms;
    private List<String> images;

    private boolean isActive;
    private boolean isVerified;
    private double rating;
    private int totalReviews;
    private String createdAt;

    // Calculated fields for frontend
    private Double distance; // Distance from search location in km
    private Integer availableBeds; // Total available beds across all rooms

    @Data
    @Builder
    public static class AddressDTO {
        private String street;
        private String landmark;
        private String area;
        private String city;
        private String state;
        private String pincode;
        private Double latitude;
        private Double longitude;
    }

    @Data
    @Builder
    public static class ContactDTO {
        private String primaryPhone;
        private String secondaryPhone;
        private String email;
    }

    @Data
    @Builder
    public static class RoomDTO {
        private String roomId;
        private String type;
        private int availableBeds;
        private int totalBeds;
        private double pricePerBed;
        private List<String> amenities;
        private List<String> images;
        private boolean isActive;
    }


}
