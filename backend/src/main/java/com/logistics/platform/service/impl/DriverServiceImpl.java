package com.logistics.platform.service.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistics.platform.dto.driver.DriverRequest;
import com.logistics.platform.dto.driver.DriverResponse;
import com.logistics.platform.entity.Driver;
import com.logistics.platform.enums.DriverStatus;
import com.logistics.platform.exception.ResourceAlreadyExistsException;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.DriverRepository;
import com.logistics.platform.service.DriverService;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    // Register a new driver
    @Override
    @Transactional
    public DriverResponse registerDriver(DriverRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        String licenseNumber = request.getLicenseNumber()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (driverRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException(
                    "A driver with this email already exists");
        }

        if (driverRepository.existsByLicenseNumber(licenseNumber)) {
            throw new ResourceAlreadyExistsException(
                    "This license number is already registered");
        }

        Driver driver = new Driver();

        driver.setName(request.getName().trim());
        driver.setEmail(email);
        driver.setPhone(request.getPhone().trim());
        driver.setVehicleType(
                request.getVehicleType().trim().toUpperCase(Locale.ROOT));
        driver.setLicenseNumber(licenseNumber);

        // New drivers start OFFLINE.
        Driver savedDriver = driverRepository.save(driver);

        return mapToResponse(savedDriver);
    }

    // Get driver by ID
    @Override
    @Transactional(readOnly = true)
    public DriverResponse getDriverById(Long id) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver not found with ID: " + id));

        return mapToResponse(driver);
    }

    // Get all drivers
    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getAllDrivers() {

        return driverRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Update driver availability
    @Override
    @Transactional
    public DriverResponse updateDriverStatus(
            Long id,
            DriverStatus status) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver not found with ID: " + id));

        driver.setStatus(status);

        Driver updatedDriver = driverRepository.save(driver);

        return mapToResponse(updatedDriver);
    }

    // Convert Driver entity to DriverResponse
    private DriverResponse mapToResponse(Driver driver) {

        return new DriverResponse(
                driver.getId(),
                driver.getName(),
                driver.getEmail(),
                driver.getPhone(),
                driver.getVehicleType(),
                driver.getLicenseNumber(),
                driver.getStatus(),
                driver.getCreatedAt(),
                driver.getUpdatedAt()
        );
    }
}