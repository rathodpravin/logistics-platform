package com.logistics.platform.service;

import com.logistics.platform.dto.auth.LoginRequest;
import com.logistics.platform.dto.auth.LoginResponse;
import com.logistics.platform.dto.auth.RegisterRequest;

public interface AuthService {

	String register(RegisterRequest request);
	LoginResponse login(LoginRequest request);
}