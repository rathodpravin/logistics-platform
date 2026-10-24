package com.logistics.platform.dto.driver;

import java.time.LocalDateTime;

import com.logistics.platform.enums.DriverStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DriverResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String vehicleType;
    private String licenseNumber;
    private DriverStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
