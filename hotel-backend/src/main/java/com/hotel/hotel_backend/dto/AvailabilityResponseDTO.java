package com.hotel.hotel_backend.dto;

import com.hotel.hotel_backend.model.HotelPackage;
import com.hotel.hotel_backend.model.RoomType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponseDTO {
    private List<RoomType> availableRoomTypes;
    private List<HotelPackage> availablePackages;
}