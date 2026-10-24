package com.logistics.platform.security;

import org.springframework.context.annotation.Bean; 

import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

	 private final JwtAuthenticationFilter jwtAuthenticationFilter;
	 @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	
	 @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http)
	            throws Exception {

	        http
	            .cors(Customizer.withDefaults())
	            .csrf(AbstractHttpConfigurer::disable)

	            .sessionManagement(session -> session
	                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	            )

	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers(
	                    "/api/health",
	                    "/api/auth/register",
	                    "/api/auth/login"
	                ).permitAll()
	                .anyRequest().authenticated()
	            )

	            .addFilterBefore(
	                jwtAuthenticationFilter,
	                UsernamePasswordAuthenticationFilter.class
	            );

	        return http.build();
	    }
}
