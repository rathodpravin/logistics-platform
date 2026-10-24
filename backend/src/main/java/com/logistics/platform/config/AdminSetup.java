package com.logistics.platform.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.logistics.platform.entity.User;
import com.logistics.platform.repository.UserRepository;

@Configuration
public class AdminSetup {

    @Bean
    CommandLineRunner createAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String adminEmail = "admin@logistics.com";

            if (!userRepository.existsByEmail(adminEmail)) {

                User admin = new User();

                admin.setName("Logistics Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(
                        passwordEncoder.encode("Admin@12345"));
                admin.setRole("ADMIN");
                admin.setActive(true);

                userRepository.save(admin);

                System.out.println("ADMIN ACCOUNT CREATED SUCCESSFULLY");

            } else {
                System.out.println("ADMIN ACCOUNT ALREADY EXISTS");
            }
        };
    }
}
