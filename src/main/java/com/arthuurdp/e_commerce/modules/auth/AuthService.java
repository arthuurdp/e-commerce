package com.arthuurdp.e_commerce.modules.auth;

import com.arthuurdp.e_commerce.modules.cart.entity.Cart;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.modules.auth.dtos.LoginRequest;
import com.arthuurdp.e_commerce.modules.auth.dtos.LoginResponse;
import com.arthuurdp.e_commerce.modules.auth.dtos.RegisterRequest;
import com.arthuurdp.e_commerce.modules.auth.dtos.RegisterResponse;
import com.arthuurdp.e_commerce.modules.user.enums.Role;
import com.arthuurdp.e_commerce.shared.exceptions.ConflictException;
import com.arthuurdp.e_commerce.infrastructure.security.TokenService;
import com.arthuurdp.e_commerce.modules.user.UserRepository;
import com.arthuurdp.e_commerce.modules.auth.mapper.AuthMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final AuthMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authManager, TokenService tokenService, UserRepository userRepository, AuthMapper mapper, PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest dto) {
        var tokenAuth = new UsernamePasswordAuthenticationToken(dto.credential(), dto.password());
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
        if (userRepository.existsByEmail(req.email()) || userRepository.existsByCpf(req.cpf())) {
            throw new ConflictException("Credentials already in use");
        }

        User user = new User(
                req.firstName(),
                req.lastName(),
                req.email(),
                passwordEncoder.encode(req.password()),
                req.cpf(),
                req.phone(),
                req.birthDate(),
                req.gender(),
                role
        );
        user.setCart(new Cart());

        return mapper.toRegisterResponse(userRepository.save(user));
    }
}
