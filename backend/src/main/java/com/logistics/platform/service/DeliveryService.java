package com.logistics.platform.service;

import java.util.List;

import com.logistics.platform.dto.delivery.DeliveryRequest;
import com.logistics.platform.dto.delivery.DeliveryResponse;
import com.logistics.platform.enums.DeliveryStatus;

public interface DeliveryService {

    DeliveryResponse createDelivery(
            DeliveryRequest request,
            String customerEmail);

    DeliveryResponse getDeliveryById(Long id);

    List<DeliveryResponse> getAllDeliveries();

    List<DeliveryResponse> getDeliveriesByCustomer(
            String customerEmail);

    List<DeliveryResponse> getDeliveriesByDriver(Long driverId);

    DeliveryResponse assignDriver(Long deliveryId, Long driverId);

    DeliveryResponse updateDeliveryStatus(
            Long deliveryId,
            DeliveryStatus status,
            String driverEmail);
    
    Long getDriverIdByEmail(String driverEmail);
}
