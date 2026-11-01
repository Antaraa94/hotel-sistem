package com.hotel.hotel_backend.repository;

import com.hotel.hotel_backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Provjera zauzetosti fizičke sobe (kod check-ina)
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.room.id = :roomId
          AND r.status IN ('CONFIRMED', 'CHECKED_IN')
          AND r.checkInDate < :checkOut
          AND r.checkOutDate > :checkIn
    """)
    List<Reservation> findOverlappingReservationsForRoom(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    // Broj rezervacija za određeni tip sobe u terminu
    @Query("""
        SELECT COUNT(r) FROM Reservation r
        WHERE r.roomType.id = :roomTypeId
          AND r.status IN ('CONFIRMED', 'CHECKED_IN')
          AND r.checkInDate < :checkOut
          AND r.checkOutDate > :checkIn
    """)
    long countActiveReservationsByRoomType(
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    List<Reservation> findByStatus(String status);
}