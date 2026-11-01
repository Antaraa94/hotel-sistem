package com.hotel.hotel_backend.controller;

import com.hotel.hotel_backend.dto.AvailabilityResponseDTO;
import com.hotel.hotel_backend.dto.CreateReservationRequest;
import com.hotel.hotel_backend.model.Reservation;
import com.hotel.hotel_backend.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // GET /api/reservations/availability?checkIn=2026-11-05&checkOut=2026-11-08
    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponseDTO> checkAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut
    ) {
        return ResponseEntity.ok(reservationService.checkAvailability(checkIn, checkOut));
    }

    // POST /api/reservations
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody CreateReservationRequest request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    // PUT /api/reservations/{id}/check-in?roomId=101
    @PutMapping("/{id}/check-in")
    public ResponseEntity<Reservation> checkIn(
            @PathVariable Long id,
            @RequestParam Long roomId
    ) {
        return ResponseEntity.ok(reservationService.checkIn(id, roomId));
    }
}