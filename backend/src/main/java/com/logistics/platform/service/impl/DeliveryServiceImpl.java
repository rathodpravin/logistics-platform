package com.logistics.platform.service.impl;

import java.util.List; 

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistics.platform.dto.delivery.DeliveryRequest;
import com.logistics.platform.dto.delivery.DeliveryResponse;
import com.logistics.platform.entity.Delivery;
import com.logistics.platform.entity.Driver;
import com.logistics.platform.entity.User;
import com.logistics.platform.enums.DeliveryStatus;
import com.logistics.platform.enums.DriverStatus;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.DeliveryRepository;
import com.logistics.platform.repository.DriverRepository;
import com.logistics.platform.repository.UserRepository;
import com.logistics.platform.service.DeliveryService;
import com.logistics.platform.service.TrackingService;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final TrackingService trackingService;

    public DeliveryServiceImpl(
            DeliveryRepository deliveryRepository,
            UserRepository userRepository,
            DriverRepository driverRepository,
            TrackingService trackingService) {

        this.deliveryRepository = deliveryRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.trackingService = trackingService;
    }

    // 1. Create a delivery
    @Override
    @Transactional
    public DeliveryResponse createDelivery(
            DeliveryRequest request,
            String customerEmail) {

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found"));

        Delivery delivery = new Delivery();

        delivery.setCustomer(customer);
        delivery.setPickupAddress(request.getPickupAddress().trim());
        delivery.setDestinationAddress(
                request.getDestinationAddress().trim());
        delivery.setPackageDescription(
                request.getPackageDescription().trim());
        delivery.setWeight(request.getWeight());
        delivery.setStatus(DeliveryStatus.CREATED);

        Delivery savedDelivery = deliveryRepository.save(delivery);

        trackingService.saveTracking(
                savedDelivery.getId(),
                DeliveryStatus.CREATED,
                "Delivery request created successfully");
        return mapToResponse(savedDelivery);
    }

    // 2. Get a delivery by ID
    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryById(Long id) {

        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery not found with ID: " + id));

        return mapToResponse(delivery);
    }

    // 3. Get all deliveries
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getAllDeliveries() {

        return deliveryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 4. Get deliveries belonging to a customer
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByCustomer(
            String customerEmail) {

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found"));

        return deliveryRepository
                .findByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 5. Get deliveries assigned to a driver
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByDriver(Long driverId) {

        return deliveryRepository
                .findByDriverIdOrderByCreatedAtDesc(driverId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 6. Assign an available driver to a delivery
    @Override
    @Transactional
    public DeliveryResponse assignDriver(
            Long deliveryId,
            Long driverId) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery not found with ID: " + deliveryId));

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver not found with ID: " + driverId));

        if (delivery.getStatus() != DeliveryStatus.CREATED) {
            throw new IllegalArgumentException(
                    "A driver can only be assigned to a CREATED delivery");
        }

        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new IllegalArgumentException(
                    "Driver must be AVAILABLE before assignment");
        }

        delivery.setDriver(driver);
        delivery.setStatus(DeliveryStatus.ASSIGNED);

        driver.setStatus(DriverStatus.BUSY);

        driverRepository.save(driver);

        Delivery savedDelivery = deliveryRepository.save(delivery);

        trackingService.saveTracking(
                savedDelivery.getId(),
                DeliveryStatus.ASSIGNED,
                "Driver assigned to delivery");
        
        return mapToResponse(savedDelivery);
    }

    // 7. Update delivery status
    @Override
    @Transactional
    public DeliveryResponse updateDeliveryStatus(
            Long deliveryId,
            DeliveryStatus status,
            String driverEmail) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery not found with ID: " + deliveryId));

        Driver driver = driverRepository.findByEmail(driverEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver account not found"));

        if (delivery.getDriver() == null
                || !delivery.getDriver().getId().equals(driver.getId())) {

            throw new AccessDeniedException(
                    "You are not assigned to this delivery");
        }

        DeliveryStatus currentStatus = delivery.getStatus();

        boolean validTransition =
                (currentStatus == DeliveryStatus.ASSIGNED
                        && status == DeliveryStatus.PICKED_UP)
                || (currentStatus == DeliveryStatus.PICKED_UP
                        && status == DeliveryStatus.IN_TRANSIT)
                || (currentStatus == DeliveryStatus.IN_TRANSIT
                        && status == DeliveryStatus.DELIVERED);

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Invalid delivery status transition from "
                            + currentStatus + " to " + status);
        }

        delivery.setStatus(status);

        if (status == DeliveryStatus.DELIVERED) {
            driver.setStatus(DriverStatus.AVAILABLE);
            driverRepository.save(driver);
        }

        Delivery savedDelivery = deliveryRepository.save(delivery);

        String message = switch (status) {
        case PICKED_UP -> "Package picked up by driver";
        case IN_TRANSIT -> "Package is on the way";
        case DELIVERED -> "Package delivered successfully";
        default -> "Delivery status updated to " + status;
    };

    trackingService.saveTracking(
            savedDelivery.getId(),
            status,
            message);
    
        return mapToResponse(savedDelivery);
    }

    // 8. Convert Delivery entity to DeliveryResponse
    private DeliveryResponse mapToResponse(Delivery delivery) {

        User customer = delivery.getCustomer();
        Driver driver = delivery.getDriver();

        return new DeliveryResponse(
                delivery.getId(),
                customer.getId(),
                customer.getName(),
                driver != null ? driver.getId() : null,
                driver != null ? driver.getName() : null,
                delivery.getPickupAddress(),
                delivery.getDestinationAddress(),
                delivery.getPackageDescription(),
                delivery.getWeight(),
                delivery.getStatus(),
                delivery.getCreatedAt(),
                delivery.getUpdatedAt()
        );
    }

	@Override
	@Transactional(readOnly = true)
	public Long getDriverIdByEmail(String driverEmail) {
		
	   Driver driver = driverRepository.findByEmail(driverEmail).orElseThrow
		          (()->new ResourceNotFoundException("Driver not fount with Email:"+driverEmail));
		return driver.getId();
	}
}