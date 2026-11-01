package com.hotel.hotel_backend.repository;

import com.hotel.hotel_backend.model.HotelPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelPackageRepository extends JpaRepository<HotelPackage, Long> {
}