package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.auth.LoginRequest;
import com.arthuurdp.e_commerce.entities.dtos.auth.LoginResponse;
import com.arthuurdp.e_commerce.entities.dtos.auth.RegisterRequest;
import com.arthuurdp.e_commerce.entities.dtos.auth.RegisterResponse;
import com.arthuurdp.e_commerce.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
        return ResponseEntity.ok().body(service.login(req));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody @Valid RegisterRequest req) {
        return ResponseEntity.status(201).body(service.registerUser(req));
    }

    @PostMapping("/register/admin")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegisterResponse> registerAdmin(@RequestBody @Valid RegisterRequest req) {
        return ResponseEntity.status(201).body(service.registerAdmin(req));
    }
}
