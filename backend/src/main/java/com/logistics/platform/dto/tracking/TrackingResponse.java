package com.logistics.platform.dto.tracking;

import java.time.LocalDateTime;

import com.logistics.platform.enums.DeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class TrackingResponse {

	    private Long deliveryId;
	    private DeliveryStatus status;
	    private String message;
	    private LocalDateTime createdAt;

}
