package com.repatroom.model.entity;

import com.repatroom.model.enums.BookingStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.KPropertyPathExtensionsKt;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;
    private String bookingId;// BR00123456 format
    private String customerId;
    private String propertyId;
    private String roomId;
    private List<Integer> bedNumbers;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int durationMonths;

    private List<Guest> guests;
    private Pricing pricing;

    private BookingStatus status;
    private String paymentStatus; // PENDING, PAID, FAILED, REFUNDED

    private String specialRequests;
    private String cancellationReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class Guest {
        private String name;
        private int age;
        private String relation; // SELF, FRIEND, FAMILY
    }

    @Data
    public static class Pricing {
        private double baseAmount;
        private double discountAmount;
        private double taxAmount;
        private double totalAmount;
        private double paidAmount;
        private double pendingAmount;
    }





}
