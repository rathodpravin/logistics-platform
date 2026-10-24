package com.logistics.platform.repository;

import java.util.List;

import com.logistics.platform.dto.delivery.DeliveryRequest;
import com.logistics.platform.dto.delivery.DeliveryResponse;

public interface DeliveryService {

	 DeliveryResponse createDelivery(DeliveryRequest request, String customerEmail);

	    DeliveryResponse getDeliveryById(Long id);

	    List<DeliveryResponse> getAllDeliveries();

	    List<DeliveryResponse> getDeliveriesByCustomer(String customerEmail);

	    List<DeliveryResponse> getDeliveriesByDriver(Long driverId);
}
