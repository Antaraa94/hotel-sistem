package com.hotel.hotel_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomNumber; // npr. 101, 102

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @Column(nullable = false)
    private String status; // AVAILABLE, OCCUPIED, CLEANING, MAINTENANCE
}