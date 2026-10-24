package com.logistics.platform.service;

import java.math.BigDecimal;
import java.util.List;

import com.logistics.platform.entity.DriverLocation;

public interface DriverLocationService {

	DriverLocation updateLocation(Long driverId, BigDecimal latitude, BigDecimal longitude);

	DriverLocation getLatestLocation(Long driverId);

	List<DriverLocation> getLocationHistory(Long driverId);
	
	Long getDriverIdByEmail(String email);
}