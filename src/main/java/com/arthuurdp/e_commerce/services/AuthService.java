package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.User;
import com.arthuurdp.e_commerce.entities.dtos.auth.*;
import com.arthuurdp.e_commerce.entities.enums.Role;
import com.arthuurdp.e_commerce.exceptions.ConflictException;
import com.arthuurdp.e_commerce.infrastructure.security.TokenService;
import com.arthuurdp.e_commerce.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final TokenService tokenService;
    private final UserRepository repo;
    private final EntityMapperService entityMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authManager,
            TokenService tokenService,
            UserRepository repo,
            EntityMapperService entityMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.authManager = authManager;
        this.tokenService = tokenService;
        this.repo = repo;
        this.entityMapper = entityMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest dto) {
        var tokenAuth = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        var auth = authManager.authenticate(tokenAuth);
        var token = tokenService.generateToken((User) auth.getPrincipal());
        return new LoginResponse(token);
    }

    public RegisterResponse registerUser(RegisterRequest req) {
        return createUser(req, Role.ROLE_USER);
    }

    public RegisterResponse registerAdmin(RegisterRequest req) {
        return createUser(req, Role.ROLE_ADMIN);
    }

    private RegisterResponse createUser(RegisterRequest req, Role role) {
        if (repo.existsByEmail(req.email())) {
            throw new ConflictException("E-mail already in use");
        }

        User user = new User(
                req.firstName(),
                req.lastName(),
                req.email(),
                passwordEncoder.encode(req.password()),
                role
        );

        repo.save(user);
        return entityMapper.toRegisterResponse(user);
    }


    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
