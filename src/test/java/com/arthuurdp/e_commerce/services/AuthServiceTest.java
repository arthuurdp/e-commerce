package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.dtos.auth.LoginRequest;
import com.arthuurdp.e_commerce.domain.dtos.auth.LoginResponse;
import com.arthuurdp.e_commerce.domain.dtos.auth.RegisterRequest;
import com.arthuurdp.e_commerce.domain.dtos.auth.RegisterResponse;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.domain.enums.Gender;
import com.arthuurdp.e_commerce.domain.enums.Role;
import com.arthuurdp.e_commerce.exceptions.ConflictException;
import com.arthuurdp.e_commerce.infrastructure.security.TokenService;
import com.arthuurdp.e_commerce.repositories.UserRepository;
import com.arthuurdp.e_commerce.services.mappers.AuthMapper;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthenticationManager authManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest buildRegisterRequest() {
        return new RegisterRequest(
                "John",
                "Doe",
                "john@example.com",
                "secret123",
                "529.982.247-25",
                "11987654321",
                LocalDate.of(1995, 6, 15),
                Gender.MALE
        );
    }

    private User buildUser(Role role) {
        return new User(
                "John",
                "Doe",
                "john@example.com",
                "encoded_secret",
                "52998224725",
                "11987654321",
                LocalDate.of(1995, 6, 15),
                Gender.MALE,
                role
        );
    }

    @Nested
    @DisplayName("login()")
    class Login {
        @Test
        @DisplayName("returns a token when credentials are valid")
        void shouldReturnTokenOnValidCredentials() {
            var request = new LoginRequest("john@example.com", "secret123");
            var user = buildUser(Role.ROLE_USER);

            Authentication auth = mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn(user);

            when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

            when(tokenService.generateToken(user)).thenReturn("fake-jwt-token");

            LoginResponse response = authService.login(request);

            assertThat(response.token()).isEqualTo("fake-jwt-token");
        }

        @Test
        @DisplayName("throws when credentials are invalid")
        void shouldThrowOnInvalidCredentials() {
            var request = new LoginRequest("john@example.com", "wrong_password");

            when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login(request)).isInstanceOf(BadCredentialsException.class);
        }
    }

    @Nested
    @DisplayName("registerUser()")
    class RegisterUser {
        @Test
        @DisplayName("saves user with ROLE_USER and returns response")
        void shouldSaveUserWithRoleUser() {
            var request = buildRegisterRequest();
            var savedUser = buildUser(Role.ROLE_USER);
            var expectedResponse = new RegisterResponse(1L, "John", "Doe", "john@example.com");

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(passwordEncoder.encode(request.password())).thenReturn("encoded_secret");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(mapper.toRegisterResponse(savedUser)).thenReturn(expectedResponse);

            RegisterResponse response = authService.registerUser(request);

            assertThat(response.email()).isEqualTo("john@example.com");
            assertThat(response.firstName()).isEqualTo("John");
        }

        @Test
        @DisplayName("encodes the password before saving")
        void shouldEncodePasswordBeforeSaving() {
            var request = buildRegisterRequest();
            var savedUser = buildUser(Role.ROLE_USER);

            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode("secret123")).thenReturn("encoded_secret");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(mapper.toRegisterResponse(any())).thenReturn(
                    new RegisterResponse(1L, "John", "Doe", "john@example.com")
            );

            authService.registerUser(request);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded_secret");
        }

        @Test
        @DisplayName("throws ConflictException when email already exists")
        void shouldThrowWhenEmailAlreadyExists() {
            var request = buildRegisterRequest();

            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            assertThatThrownBy(() -> authService.registerUser(request)).isInstanceOf(ConflictException.class).hasMessage("E-mail already in use");

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("registerAdmin()")
    class RegisterAdmin {
        @Test
        @DisplayName("saves user with ROLE_ADMIN")
        void shouldSaveUserWithRoleAdmin() {
            var request = buildRegisterRequest();
            var savedUser = buildUser(Role.ROLE_ADMIN);

            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encoded_secret");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(mapper.toRegisterResponse(any())).thenReturn(
                    new RegisterResponse(1L, "John", "Doe", "john@example.com")
            );

            authService.registerAdmin(request);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.ROLE_ADMIN);
        }
    }
}
