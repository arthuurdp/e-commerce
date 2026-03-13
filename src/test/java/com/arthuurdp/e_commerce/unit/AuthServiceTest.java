package com.arthuurdp.e_commerce.unit;

import com.arthuurdp.e_commerce.modules.auth.dtos.LoginRequest;
import com.arthuurdp.e_commerce.modules.auth.dtos.LoginResponse;
import com.arthuurdp.e_commerce.modules.auth.dtos.RegisterRequest;
import com.arthuurdp.e_commerce.modules.auth.dtos.RegisterResponse;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.modules.user.enums.Gender;
import com.arthuurdp.e_commerce.modules.user.enums.Role;
import com.arthuurdp.e_commerce.shared.exceptions.ConflictException;
import com.arthuurdp.e_commerce.infrastructure.security.TokenService;
import com.arthuurdp.e_commerce.modules.auth.AuthService;
import com.arthuurdp.e_commerce.modules.user.UserRepository;
import com.arthuurdp.e_commerce.modules.auth.mapper.AuthMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authManager;
    @Mock private TokenService tokenService;
    @Mock private UserRepository userRepository;
    @Mock private AuthMapper mapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User user;
    private RegisterResponse registerResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "John", "Doe", "john@example.com", "secret123",
                "529.982.247-25", "11987654321",
                LocalDate.of(1995, 6, 15), Gender.MALE
        );

        user = new User(
                "John", "Doe", "john@example.com", "encoded_secret",
                "52998224725", "11987654321",
                LocalDate.of(1995, 6, 15), Gender.MALE, Role.ROLE_USER
        );

        registerResponse = new RegisterResponse(1L, "John", "Doe", "john@example.com");
    }

    @Nested
    @DisplayName("login()")
    class Login {

        @Test
        @DisplayName("returns token when credentials are valid")
        void shouldReturnTokenOnValidCredentials() {
            Authentication auth = mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn(user);
            when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
            when(tokenService.generateToken(user)).thenReturn("fake-jwt-token");

            LoginResponse response = authService.login(new LoginRequest("john@example.com", "secret123"));

            assertThat(response.token()).isEqualTo("fake-jwt-token");
            verify(tokenService).generateToken(user);
        }

        @Test
        @DisplayName("throws BadCredentialsException when credentials are invalid")
        void shouldThrowOnInvalidCredentials() {
            when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login(new LoginRequest("john@example.com", "wrong"))).isInstanceOf(BadCredentialsException.class).hasMessage("Bad credentials");

            verifyNoInteractions(tokenService);
        }
    }

    @Nested
    @DisplayName("registerUser()")
    class RegisterUser {

        @Test
        @DisplayName("saves user with ROLE_USER and returns response")
        void shouldSaveUserWithRoleUser() {
            when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(registerRequest.password())).thenReturn("encoded_secret");
            when(mapper.toRegisterResponse(any(User.class))).thenReturn(registerResponse);

            RegisterResponse response = authService.registerUser(registerRequest);

            assertThat(response.email()).isEqualTo("john@example.com");
            assertThat(response.firstName()).isEqualTo("John");
        }

        @Test
        @DisplayName("encodes password before saving")
        void shouldEncodePasswordBeforeSaving() {
            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode("secret123")).thenReturn("encoded_secret");
            when(mapper.toRegisterResponse(any())).thenReturn(registerResponse);

            authService.registerUser(registerRequest);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("encoded_secret");
        }

        @Test
        @DisplayName("assigns a Cart to the user before saving")
        void shouldAssignCartToUser() {
            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encoded_secret");
            when(mapper.toRegisterResponse(any())).thenReturn(registerResponse);

            authService.registerUser(registerRequest);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getCart()).isNotNull();
        }

        @Test
        @DisplayName("throws ConflictException when email already exists")
        void shouldThrowWhenEmailAlreadyExists() {
            when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

            assertThatThrownBy(() -> authService.registerUser(registerRequest)).isInstanceOf(ConflictException.class).hasMessage("E-mail already in use");

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("registerAdmin()")
    class RegisterAdmin {

        @Test
        @DisplayName("saves user with ROLE_ADMIN")
        void shouldSaveUserWithRoleAdmin() {
            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encoded_secret");
            when(mapper.toRegisterResponse(any())).thenReturn(registerResponse);

            authService.registerAdmin(registerRequest);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getRole()).isEqualTo(Role.ROLE_ADMIN);
        }

        @Test
        @DisplayName("throws ConflictException when email already exists")
        void shouldThrowWhenEmailAlreadyExists() {
            when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

            assertThatThrownBy(() -> authService.registerAdmin(registerRequest)).isInstanceOf(ConflictException.class).hasMessage("E-mail already in use");

            verify(userRepository, never()).save(any());
        }
    }
}