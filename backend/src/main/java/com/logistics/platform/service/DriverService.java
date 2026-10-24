package com.logistics.platform.service;

import java.util.List;


import com.logistics.platform.dto.driver.DriverRequest;
import com.logistics.platform.dto.driver.DriverResponse;

import com.logistics.platform.enums.DriverStatus;

public interface DriverService {

	DriverResponse registerDriver(DriverRequest request);

	DriverResponse getDriverById(Long id);

	List<DriverResponse> getAllDrivers();

	DriverResponse updateDriverStatus(Long id, DriverStatus status);
}
