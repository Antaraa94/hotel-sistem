package com.hotel.hotel_backend;

import com.hotel.hotel_backend.model.HotelPackage;
import com.hotel.hotel_backend.model.Room;
import com.hotel.hotel_backend.model.RoomType;
import com.hotel.hotel_backend.repository.HotelPackageRepository;
import com.hotel.hotel_backend.repository.RoomRepository;
import com.hotel.hotel_backend.repository.RoomTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final HotelPackageRepository hotelPackageRepository;

    public DataInitializer(
            RoomTypeRepository roomTypeRepository,
            RoomRepository roomRepository,
            HotelPackageRepository hotelPackageRepository
    ) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.hotelPackageRepository = hotelPackageRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Inicijalizacija paketa (Room Only vs Bed & Breakfast)
        if (hotelPackageRepository.count() == 0) {
            HotelPackage p1 = new HotelPackage(null, "Noćenje", new BigDecimal("0.00"));
            HotelPackage p2 = new HotelPackage(null, "Noćenje s doručkom", new BigDecimal("15.00"));

            hotelPackageRepository.saveAll(List.of(p1, p2));
            System.out.println(">>> Uspješno ubačeni paketi! <<<");
        }

        // 2. Inicijalizacija tipova soba i fizičkih soba (Opera koncept)
        if (roomTypeRepository.count() == 0) {
            RoomType single = new RoomType(null, "Single Room", 1, new BigDecimal("50.00"));
            RoomType doubleRoom = new RoomType(null, "Double Room", 2, new BigDecimal("80.00"));
            RoomType deluxe = new RoomType(null, "Deluxe Suite", 4, new BigDecimal("150.00"));

            roomTypeRepository.saveAll(List.of(single, doubleRoom, deluxe));

            // Fizičke sobe povezane s tipom sobe
            Room r101 = new Room(null, "101", single, "AVAILABLE");
            Room r102 = new Room(null, "102", single, "AVAILABLE");
            Room r201 = new Room(null, "201", doubleRoom, "AVAILABLE");
            Room r202 = new Room(null, "202", doubleRoom, "AVAILABLE");
            Room r301 = new Room(null, "301", deluxe, "AVAILABLE");

            roomRepository.saveAll(List.of(r101, r102, r201, r202, r301));
            System.out.println(">>> Uspješno ubačeni tipovi soba i fizičke sobe! <<<");
        }
    }
}