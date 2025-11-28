package com.repatroom.model.entity;

import com.repatroom.model.enums.PropertyType;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collation = "properties")
public class Property {

    @Id
    private String id;
    private String ownerId;
    private String name;
    private String description;
    private PropertyType type;
    private String category; // BOYS, GIRLS, FAMILY, BACHELOR, CO_LIVING

    //Addres
    private Address address;
    private Contact contact;
    private List<String> facilities;
    private List<String> amenities;
    private List<Room> rooms;
    private List<String> images;

    private boolean isActive = true;
    private boolean isVerified = false;
    private double rating = 0.0;
    private int totalReviews = 0;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class Address
    {
        private String street;
        private String landmark;
        private String area;
        private String city;
        private String state;
        private String pincode;

        @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
        private GeoJsonPoint location;

    }

    @Data
    public static class Contact {
        private String primaryPhone;
        private String secondaryPhone;
        private String email;
    }


    @Data
    public static class Room{
        private String roomId;
        private String type; // SINGLE, SHARING_2, SHARING_3, SHARING_4
        private int availableBeds;
        private int totalBeds;
        private double pricePerBed;
        private List<String> amenities;
        private List<String> images;
        private boolean isActive = true;

    }


}
