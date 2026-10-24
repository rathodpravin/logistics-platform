package com.logistics.platform.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.logistics.platform.entity.Driver;
import com.logistics.platform.entity.User;
import com.logistics.platform.repository.DriverRepository;
import com.logistics.platform.repository.UserRepository;

@Configuration
public class DriverSetup {

    @Bean
    CommandLineRunner createDriverAccount(
            DriverRepository driverRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String driverEmail = "driver2@logistics.com";

            Driver driver = driverRepository.findByEmail(driverEmail)
                    .orElseThrow(() -> new IllegalStateException(
                            "Driver record not found: " + driverEmail));

            if (!userRepository.existsByEmail(driverEmail)) {

                User user = new User();

                user.setName(driver.getName());
                user.setEmail(driverEmail);
                user.setPassword(
                        passwordEncoder.encode("Driver@12345"));
                user.setRole("DRIVER");
                user.setActive(true);

                userRepository.save(user);

                System.out.println(
                        "DRIVER ACCOUNT CREATED SUCCESSFULLY");

            } else {

                System.out.println(
                        "USER ACCOUNT ALREADY EXISTS: " + driverEmail);
            }
        };
    }
}