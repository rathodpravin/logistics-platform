package com.logistics.platform.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistics.platform.dto.tracking.TrackingResponse;
import com.logistics.platform.entity.Delivery;
import com.logistics.platform.entity.DeliveryTracking;
import com.logistics.platform.enums.DeliveryStatus;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.DeliveryRepository;
import com.logistics.platform.repository.DeliveryTrackingRepository;
import com.logistics.platform.service.TrackingService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TrackingServiceImpl implements TrackingService  {

	private final DeliveryRepository deliveryRepository;
    private final DeliveryTrackingRepository trackingRepository;
  
	
	@Override
	public List<TrackingResponse> getTrackingHistory(Long deliveryId) {
	Delivery delivery=	deliveryRepository.findById(deliveryId).orElseThrow(() ->
            new ResourceNotFoundException(
                "Delivery not found with ID: " + deliveryId));

	  List<DeliveryTracking> trackingList =
			   trackingRepository.findByDeliveryIdOrderByCreatedAtAsc(delivery.getId());
	  
	  
		return trackingList.stream()
				.map(tracking ->new TrackingResponse(
						tracking.getDelivery().getId(),
						tracking.getStatus(),
						tracking.getMessage(),
						tracking.getCreatedAt()))
				.toList();
	}

	@Override
	@Transactional
	public void saveTracking(
	        Long deliveryId,
	        DeliveryStatus status,
	        String message) {

	    Delivery delivery = deliveryRepository.findById(deliveryId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Delivery not found with ID: " + deliveryId));

	    DeliveryTracking tracking = new DeliveryTracking();
	    tracking.setDelivery(delivery);
	    tracking.setStatus(status);
	    tracking.setMessage(message);

	    trackingRepository.save(tracking);
	}

}
