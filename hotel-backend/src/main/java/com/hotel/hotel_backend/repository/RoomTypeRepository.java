package com.hotel.hotel_backend.repository;

import com.hotel.hotel_backend.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    @Query("""
        SELECT rt FROM RoomType rt
        WHERE (
            SELECT COUNT(r) FROM Room r 
            WHERE r.roomType.id = rt.id 
              AND r.status != 'MAINTENANCE'
        ) > (
            SELECT COUNT(res) FROM Reservation res 
            WHERE res.roomType.id = rt.id 
              AND res.status IN ('CONFIRMED', 'CHECKED_IN')
              AND res.checkInDate < :checkOut 
              AND res.checkOutDate > :checkIn
        )
    """)
    List<RoomType> findAvailableRoomTypes(
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}