package com.logistics.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistics.platform.entity.DeliveryTracking;

public interface DeliveryTrackingRepository extends JpaRepository 
                                                 <DeliveryTracking, Long> {

List<DeliveryTracking> findByDeliveryIdOrderByCreatedAtAsc(Long deliveryId);
}
