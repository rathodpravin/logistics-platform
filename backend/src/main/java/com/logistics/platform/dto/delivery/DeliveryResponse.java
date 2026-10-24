package com.logistics.platform.dto.delivery;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.logistics.platform.enums.DeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryResponse {

    private Long id;

    private Long customerId;

    private String customerName;

    private Long driverId;

    private String driverName;

    private String pickupAddress;

    private String destinationAddress;

    private String packageDescription;

    private BigDecimal weight;

    private DeliveryStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
