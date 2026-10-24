package com.logistics.platform.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;

@RestController
public class HealthController {
	
	 @GetMapping("/api/health")
	    public Map<String, String> checkHealth() {

	        return Map.of(
	                "status", "UP",
	                "application", "logistics-platform",
	                "message", "Logistics Platform API is running successfully"
	        );
	    }
	    
	    @GetMapping("/api/health/auth-check")
	    public Map<String, Object> checkAuthentication(
	            Authentication authentication) {

	        return Map.of(
	            "email", authentication.getName(),
	            "authorities", authentication.getAuthorities()
	                    .stream()
	                    .map(Object::toString)
	                    .collect(Collectors.toList())
	        );
	    }

}
