package com.hotel.hotel_backend.controller;

import com.hotel.hotel_backend.model.HotelPackage;
import com.hotel.hotel_backend.model.Room;
import com.hotel.hotel_backend.model.RoomType;
import com.hotel.hotel_backend.repository.HotelPackageRepository;
import com.hotel.hotel_backend.repository.RoomRepository;
import com.hotel.hotel_backend.repository.RoomTypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final HotelPackageRepository hotelPackageRepository;

    public AdminController(
            RoomTypeRepository roomTypeRepository,
            RoomRepository roomRepository,
            HotelPackageRepository hotelPackageRepository
    ) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.hotelPackageRepository = hotelPackageRepository;
    }

    // --- Upravljanje vrstama soba (Room Types) i cijenama ---
    @GetMapping("/room-types")
    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    @PostMapping("/room-types")
    public RoomType createRoomType(@RequestBody RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    @PutMapping("/room-types/{id}/price")
    public ResponseEntity<RoomType> updateRoomTypePrice(
            @PathVariable Long id,
            @RequestParam BigDecimal newPrice
    ) {
        return roomTypeRepository.findById(id)
                .map(rt -> {
                    rt.setBasePrice(newPrice);
                    return ResponseEntity.ok(roomTypeRepository.save(rt));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Upravljanje fizičkim sobama ---
    @GetMapping("/rooms")
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @PostMapping("/rooms")
    public ResponseEntity<Room> createRoom(
            @RequestParam String roomNumber,
            @RequestParam Long roomTypeId
    ) {
        return roomTypeRepository.findById(roomTypeId)
                .map(type -> {
                    Room room = new Room(null, roomNumber, type, "AVAILABLE");
                    return ResponseEntity.ok(roomRepository.save(room));
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    // --- Upravljanje paketima (Packages) ---
    @GetMapping("/packages")
    public List<HotelPackage> getAllPackages() {
        return hotelPackageRepository.findAll();
    }

    @PostMapping("/packages")
    public HotelPackage createPackage(@RequestBody HotelPackage hotelPackage) {
        return hotelPackageRepository.save(hotelPackage);
    }
}