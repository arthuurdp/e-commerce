package com.arthuurdp.e_commerce.modules.auth;

import com.arthuurdp.e_commerce.modules.auth.dtos.LoginRequest;
import com.arthuurdp.e_commerce.modules.auth.dtos.LoginResponse;
import com.arthuurdp.e_commerce.modules.auth.dtos.RegisterRequest;
import com.arthuurdp.e_commerce.modules.auth.dtos.RegisterResponse;
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
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest req
    ) {
        return ResponseEntity.ok().body(service.login(req));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(
            @RequestBody @Valid RegisterRequest req
    ) {
        return ResponseEntity.status(201).body(service.registerUser(req));
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegisterResponse> registerAdmin(
            @RequestBody @Valid RegisterRequest req
    ) {
        return ResponseEntity.status(201).body(service.registerAdmin(req));
    }
}