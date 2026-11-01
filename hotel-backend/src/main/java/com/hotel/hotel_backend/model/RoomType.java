package com.hotel.hotel_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "room_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // npr. Single Room, Double Room, Deluxe Suite

    @Column(nullable = false)
    private Integer capacity; // broj osoba

    @Column(nullable = false)
    private BigDecimal basePrice; // osnovna cijena po noćenju
}