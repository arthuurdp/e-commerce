package com.arthuurdp.e_commerce.infrastructure.config;

import com.arthuurdp.e_commerce.entities.dtos.auth.RegisterRequest;
import com.arthuurdp.e_commerce.repositories.UserRepository;
import com.arthuurdp.e_commerce.services.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    private final UserRepository userRepository;
    private final AuthService authService;

    public DataInitializer(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }
    
    @Bean
    CommandLineRunner initUsers() {
        return args -> {
            if (!userRepository.existsByEmail("admin@gmail.com")) {
                authService.registerAdmin(new RegisterRequest(
                        "Admin", "User", "admin@gmail.com", "admin123"
                ));
            }

            if (!userRepository.existsByEmail("user@gmail.com")) {
                authService.registerUser(new RegisterRequest(
                        "Regular", "User", "user@gmail.com", "user123"
                ));
            }
        };
    }
}
