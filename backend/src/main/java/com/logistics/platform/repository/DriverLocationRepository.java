package com.logistics.platform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistics.platform.entity.DriverLocation;

public interface DriverLocationRepository
        extends JpaRepository<DriverLocation, Long> {

    Optional<DriverLocation> findTopByDriver_IdOrderByRecordedAtDesc(Long driverId);

    List<DriverLocation> findByDriver_IdOrderByRecordedAtDesc(Long driverId);
}