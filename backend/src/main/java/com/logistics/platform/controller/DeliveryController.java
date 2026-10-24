package com.logistics.platform.controller;

import java.util.List; 

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistics.platform.dto.delivery.DeliveryRequest;
import com.logistics.platform.dto.delivery.DeliveryResponse;
import com.logistics.platform.enums.DeliveryStatus;
import com.logistics.platform.service.DeliveryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // Create delivery
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<DeliveryResponse> createDelivery(
            @Valid @RequestBody DeliveryRequest request,
            Authentication authentication) {

        DeliveryResponse response = deliveryService.createDelivery(
                request,
                authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get current customer's deliveries
    @GetMapping("/customer/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<DeliveryResponse>> getMyDeliveries(
            Authentication authentication) {

        return ResponseEntity.ok(
                deliveryService.getDeliveriesByCustomer(
                        authentication.getName()));
    }

    // Get deliveries assigned to a driver
    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<DeliveryResponse>> getDriverDeliveries(
            @PathVariable Long driverId,
            Authentication authentication) {

   Long authenticatedDriverId =  deliveryService.getDriverIdByEmail(authentication.getName());
    	       if(! authenticatedDriverId.equals(driverId)) {
    	    	            throw new org.springframework.security.access.AccessDeniedException(
    	                             "You can only access your own deliveries");
    	       }
    	   
        return ResponseEntity.ok(
                deliveryService.getDeliveriesByDriver(driverId));
    }

    // Get delivery by ID (admin only)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryResponse> getDeliveryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                deliveryService.getDeliveryById(id));
    }

    // Get all deliveries (admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {

        return ResponseEntity.ok(
                deliveryService.getAllDeliveries());
    }

    // Assign driver to delivery (admin only)
    @PutMapping("/{deliveryId}/assign/{driverId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryResponse> assignDriver(
            @PathVariable Long deliveryId,
            @PathVariable Long driverId) {

        return ResponseEntity.ok(
                deliveryService.assignDriver(deliveryId, driverId));
    }

    // Update delivery status (assigned driver only)
    @PutMapping("/{deliveryId}/status")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DeliveryResponse> updateDeliveryStatus(
            @PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status,
            Authentication authentication) {

        System.out.println("Authenticated email: " + authentication.getName());
        System.out.println("Authorities: " + authentication.getAuthorities());

        return ResponseEntity.ok(
                deliveryService.updateDeliveryStatus(
                        deliveryId,
                        status,
                        authentication.getName()));
    }
}