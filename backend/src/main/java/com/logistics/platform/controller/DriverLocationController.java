package com.logistics.platform.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistics.platform.entity.DriverLocation;
import com.logistics.platform.service.DriverLocationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/locations")
public class DriverLocationController {

    private final DriverLocationService driverLocationService;

    public DriverLocationController(
            DriverLocationService driverLocationService) {
        this.driverLocationService = driverLocationService;
    }

    // Driver updates their own location
    @PostMapping
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverLocation> updateLocation(
            @Valid @RequestBody LocationRequest request,
            Authentication authentication) {

        // Resolve the authenticated driver rather than trusting a
        // driverId supplied by the client.
        Long driverId = driverLocationService
                .getDriverIdByEmail(authentication.getName());

        DriverLocation location = driverLocationService.updateLocation(
                driverId,
                request.getLatitude(),
                request.getLongitude());

        return ResponseEntity.status(HttpStatus.CREATED).body(location);
    }

    // Get the latest location of a driver
    @GetMapping("/driver/{driverId}/latest")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<DriverLocation> getLatestLocation(
            @PathVariable Long driverId,
            Authentication authentication) {

        // Additional ownership/assignment authorization will be added
        // before enabling access for customers and drivers.
        return ResponseEntity.ok(
                driverLocationService.getLatestLocation(driverId));
    }

    // Get location history for a driver
    @GetMapping("/driver/{driverId}/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DriverLocation>> getLocationHistory(
            @PathVariable Long driverId) {

        return ResponseEntity.ok(
                driverLocationService.getLocationHistory(driverId));
    }

    public static class LocationRequest {

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be at least -90")
        @DecimalMax(value = "90.0", message = "Latitude must not exceed 90")
        private BigDecimal latitude;

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be at least -180")
        @DecimalMax(value = "180.0", message = "Longitude must not exceed 180")
        private BigDecimal longitude;

        public BigDecimal getLatitude() {
            return latitude;
        }

        public void setLatitude(BigDecimal latitude) {
            this.latitude = latitude;
        }

        public BigDecimal getLongitude() {
            return longitude;
        }

        public void setLongitude(BigDecimal longitude) {
            this.longitude = longitude;
        }
    }
}
