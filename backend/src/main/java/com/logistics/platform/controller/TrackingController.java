package com.logistics.platform.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistics.platform.dto.delivery.DeliveryResponse;
import com.logistics.platform.dto.tracking.TrackingResponse;
import com.logistics.platform.service.DeliveryService;
import com.logistics.platform.service.TrackingService;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final TrackingService trackingService;
    private final DeliveryService deliveryService;

    public TrackingController(
            TrackingService trackingService,
            DeliveryService deliveryService) {

        this.trackingService = trackingService;
        this.deliveryService = deliveryService;
    }

    @GetMapping("/{deliveryId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<List<TrackingResponse>> getTrackingHistory(
            @PathVariable Long deliveryId,
            Authentication authentication) {

        // Administrators can view any delivery's tracking history.
        if (authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"))) {

            return ResponseEntity.ok(
                    trackingService.getTrackingHistory(deliveryId));
        }

        // Get the delivery details.
        DeliveryResponse delivery =
                deliveryService.getDeliveryById(deliveryId);

        boolean hasAccess = false;

        // Customers can view only their own deliveries.
        if (authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_CUSTOMER"))) {

            hasAccess = deliveryService
                    .getDeliveriesByCustomer(authentication.getName())
                    .stream()
                    .anyMatch(item ->
                            item.getId().equals(deliveryId));
        }

        // Drivers can view only deliveries assigned to them.
        if (authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_DRIVER"))) {

            Long driverId =
                    deliveryService.getDriverIdByEmail(
                            authentication.getName());

            hasAccess = delivery.getDriverId() != null
                    && delivery.getDriverId().equals(driverId);
        }

        if (!hasAccess) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        return ResponseEntity.ok(
                trackingService.getTrackingHistory(deliveryId));
    }
}