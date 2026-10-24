package com.logistics.platform.service;

import java.util.List;

import com.logistics.platform.dto.tracking.TrackingResponse;
import com.logistics.platform.enums.DeliveryStatus;

public interface TrackingService {

	List<TrackingResponse> getTrackingHistory(Long deliveryId);

	void saveTracking(Long deliveryId, DeliveryStatus status, String message);
}
