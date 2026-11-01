package com.hotel.hotel_backend.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {
    private Long roomTypeId;
    private Long packageId;
    private String firstName;
    private String lastName;
    private String guestEmail;
    private String phoneNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}