package com.logistics.platform.dto.delivery;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeliveryRequest {

    @NotBlank(message = "Pickup address is required")
    @Size(max = 255, message = "Pickup address cannot exceed 255 characters")
    private String pickupAddress;

    @NotBlank(message = "Destination address is required")
    @Size(max = 255, message = "Destination address cannot exceed 255 characters")
    private String destinationAddress;

    @NotBlank(message = "Package description is required")
    @Size(max = 255, message = "Package description cannot exceed 255 characters")
    private String packageDescription;

    @NotNull(message = "Package weight is required")
    @DecimalMin(value = "0.01", message = "Weight must be greater than zero")
    private BigDecimal weight;
}