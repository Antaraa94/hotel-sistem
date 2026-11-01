package com.hotel.hotel_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Vrsta sobe koju je gost zakupio (obavezno od samog početka)
    @ManyToOne(optional = false)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    // Fizička soba dodjeljuje se tek na check-in-u (može biti null dok je samo rezervirano)
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = true)
    private Room room;

    @ManyToOne(optional = false)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    // Odabrani paket (npr. samo noćenje ili noćenje s doručkom)
    @ManyToOne(optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private HotelPackage hotelPackage;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String status; // CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
}