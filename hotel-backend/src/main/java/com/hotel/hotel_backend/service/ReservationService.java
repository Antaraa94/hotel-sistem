package com.hotel.hotel_backend.service;

import com.hotel.hotel_backend.dto.AvailabilityResponseDTO;
import com.hotel.hotel_backend.dto.CreateReservationRequest;
import com.hotel.hotel_backend.model.*;
import com.hotel.hotel_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationService {

    private final RoomTypeRepository roomTypeRepository;
    private final HotelPackageRepository hotelPackageRepository;
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;

    public ReservationService(
            RoomTypeRepository roomTypeRepository,
            HotelPackageRepository hotelPackageRepository,
            ReservationRepository reservationRepository,
            GuestRepository guestRepository,
            RoomRepository roomRepository
    ) {
        this.roomTypeRepository = roomTypeRepository;
        this.hotelPackageRepository = hotelPackageRepository;
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
    }

    // 1. Pretraga dostupnosti za odabrane datume
    public AvailabilityResponseDTO checkAvailability(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            throw new IllegalArgumentException("Datum odjave mora biti nakon datuma prijave.");
        }

        List<RoomType> availableTypes = roomTypeRepository.findAvailableRoomTypes(checkIn, checkOut);
        List<HotelPackage> packages = hotelPackageRepository.findAll();

        return new AvailabilityResponseDTO(availableTypes, packages);
    }

    // 2. Kreiranje rezervacije s odabranim paketom
    @Transactional
    public Reservation createReservation(CreateReservationRequest request) {
        if (request.getCheckInDate().isAfter(request.getCheckOutDate()) || request.getCheckInDate().isEqual(request.getCheckOutDate())) {
            throw new IllegalArgumentException("Datum odjave mora biti nakon datuma prijave.");
        }

        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Tip sobe nije pronađen."));

        HotelPackage hotelPackage = hotelPackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new IllegalArgumentException("Paket nije pronađen."));

        // Provjera ima li slobodnih soba za taj tip sobe u terminu
        List<RoomType> availableTypes = roomTypeRepository.findAvailableRoomTypes(request.getCheckInDate(), request.getCheckOutDate());
        boolean isAvailable = availableTypes.stream().anyMatch(rt -> rt.getId().equals(roomType.getId()));
        if (!isAvailable) {
            throw new IllegalStateException("Nema slobodnih soba za odabrani tip u tom terminu.");
        }

        // Gost: pronalazak postojećeg po emailu ili kreiranje novog
        Guest guest = guestRepository.findByEmail(request.getGuestEmail())
                .orElseGet(() -> {
                    Guest newGuest = new Guest();
                    newGuest.setFirstName(request.getFirstName());
                    newGuest.setLastName(request.getLastName());
                    newGuest.setEmail(request.getGuestEmail());
                    newGuest.setPhoneNumber(request.getPhoneNumber());
                    return guestRepository.save(newGuest);
                });

        // Izračun ukupne cijene: (osnovna cijena sobe + cijena paketa) * broj noćenja
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal pricePerNight = roomType.getBasePrice().add(hotelPackage.getExtraPricePerNight());
        BigDecimal totalPrice = pricePerNight.multiply(BigDecimal.valueOf(nights));

        Reservation reservation = new Reservation();
        reservation.setRoomType(roomType);
        reservation.setRoom(null); // Soba se dodjeljuje na check-inu (Oracle Opera flow)
        reservation.setGuest(guest);
        reservation.setHotelPackage(hotelPackage);
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setTotalPrice(totalPrice);
        reservation.setStatus("CONFIRMED");

        return reservationRepository.save(reservation);
    }

    // 3. Check-in akcija (recepcija dodjeljuje fizički broj sobe)
    @Transactional
    public Reservation checkIn(Long reservationId, Long roomId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Rezervacija nije pronađena."));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Soba nije pronađena."));

        if (!room.getRoomType().getId().equals(res.getRoomType().getId())) {
            throw new IllegalArgumentException("Dodijeljena soba ne odgovara rezerviranom tipu sobe.");
        }

        List<Reservation> conflicts = reservationRepository.findOverlappingReservationsForRoom(
                roomId, res.getCheckInDate(), res.getCheckOutDate()
        );
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Ova soba je već zauzeta u tom terminu.");
        }

        res.setRoom(room);
        res.setStatus("CHECKED_IN");
        room.setStatus("OCCUPIED");
        roomRepository.save(room);

        return reservationRepository.save(res);
    }
}