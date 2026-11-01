package com.hotel.hotel_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // npr. Noćenje (Room Only), Noćenje s doručkom (Bed & Breakfast)

    @Column(nullable = false)
    private BigDecimal extraPricePerNight; // npr. 0.00 ili 15.00
}