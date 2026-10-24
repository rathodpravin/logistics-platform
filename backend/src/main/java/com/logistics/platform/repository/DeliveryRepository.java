package com.logistics.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistics.platform.entity.Delivery;
import com.logistics.platform.enums.DeliveryStatus;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    List<Delivery> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Delivery> findByDriverIdOrderByCreatedAtDesc(Long driverId);

    List<Delivery> findByStatus(DeliveryStatus status);
}