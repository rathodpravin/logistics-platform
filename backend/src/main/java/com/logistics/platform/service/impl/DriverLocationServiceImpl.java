package com.logistics.platform.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistics.platform.entity.Driver;
import com.logistics.platform.entity.DriverLocation;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.DriverLocationRepository;
import com.logistics.platform.repository.DriverRepository;
import com.logistics.platform.service.DriverLocationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
public class DriverLocationServiceImpl implements DriverLocationService {

    private final DriverLocationRepository driverLocationRepository;
    private final DriverRepository driverRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public DriverLocationServiceImpl(
            DriverLocationRepository driverLocationRepository,
            DriverRepository driverRepository,
            SimpMessagingTemplate messagingTemplate) {

        this.driverLocationRepository = driverLocationRepository;
        this.driverRepository = driverRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional
    public DriverLocation updateLocation(
            Long driverId,
            BigDecimal latitude,
            BigDecimal longitude) {

        validateCoordinates(latitude, longitude);

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver not found with ID: " + driverId));

        DriverLocation location = new DriverLocation();
        location.setDriver(driver);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        DriverLocation savedLocation = driverLocationRepository.save(location);

     // Publish the saved location to subscribers of this driver's topic.
     messagingTemplate.convertAndSend(
             "/topic/driver/" + driverId + "/location",
             savedLocation
     );

     return savedLocation;
    }

    @Override
    @Transactional(readOnly = true)
    public DriverLocation getLatestLocation(Long driverId) {

        if (!driverRepository.existsById(driverId)) {
            throw new ResourceNotFoundException(
                    "Driver not found with ID: " + driverId);
        }

        return driverLocationRepository
                .findTopByDriver_IdOrderByRecordedAtDesc(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No location found for driver ID: " + driverId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverLocation> getLocationHistory(Long driverId) {

        if (!driverRepository.existsById(driverId)) {
            throw new ResourceNotFoundException(
                    "Driver not found with ID: " + driverId);
        }

        return driverLocationRepository
                .findByDriver_IdOrderByRecordedAtDesc(driverId);
    }

    private void validateCoordinates(
            BigDecimal latitude,
            BigDecimal longitude) {

        if (latitude == null
                || latitude.compareTo(BigDecimal.valueOf(-90)) < 0
                || latitude.compareTo(BigDecimal.valueOf(90)) > 0) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90");
        }

        if (longitude == null
                || longitude.compareTo(BigDecimal.valueOf(-180)) < 0
                || longitude.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180");
        }
    }
    @Override
    @Transactional(readOnly = true)
    public Long getDriverIdByEmail(String email) {

        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver account not found"));

        return driver.getId();
    }
}
