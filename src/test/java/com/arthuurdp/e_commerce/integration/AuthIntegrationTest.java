package com.arthuurdp.e_commerce.integration;

import com.arthuurdp.e_commerce.modules.auth.dtos.LoginRequest;
import com.arthuurdp.e_commerce.modules.auth.dtos.RegisterRequest;
import com.arthuurdp.e_commerce.modules.user.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Auth Integration Tests")
class AuthIntegrationTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("POST /auth/register")
    class Register {

        @Test
        @DisplayName("returns 201 and user data when request is valid")
        void shouldRegisterSuccessfully() throws Exception {
            RegisterRequest req = new RegisterRequest(
                    "Jane", "Doe", "jane@test.com", "password123",
                    "39209275080", "11987654321",
                    LocalDate.of(1995, 6, 15), Gender.FEMALE
            );

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value("jane@test.com"))
                    .andExpect(jsonPath("$.firstName").value("Jane"))
                    .andExpect(jsonPath("$.id").isNumber());
        }

        @Test
        @DisplayName("returns 409 when email is already taken")
        void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
            RegisterRequest req = new RegisterRequest(
                    "Dup", "User", "user@test.com", "password123",
                    "111.444.777-35", "11987654321",
                    LocalDate.of(1995, 6, 15), Gender.MALE
            );

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("Conflict"));
        }

        @Test
        @DisplayName("returns 400 when required fields are missing")
        void shouldReturnBadRequestWhenFieldsMissing() throws Exception {
            String body = """
                    {
                        "firstName": "Jane"
                    }
                    """;

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Error"));
        }

        @Test
        @DisplayName("returns 400 when CPF is invalid")
        void shouldReturnBadRequestWhenCpfIsInvalid() throws Exception {
            RegisterRequest req = new RegisterRequest(
                    "Jane", "Doe", "jane2@test.com", "password123",
                    "111.111.111-11", "11987654321",
                    LocalDate.of(1995, 6, 15), Gender.FEMALE
            );

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Error"));
        }

        @Test
        @DisplayName("returns 400 when email format is invalid")
        void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
            RegisterRequest req = new RegisterRequest(
                    "Jane", "Doe", "not-an-email", "password123",
                    "529.982.247-25", "11987654321",
                    LocalDate.of(1995, 6, 15), Gender.FEMALE
            );

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /auth/login")
    class Login {

        @Test
        @DisplayName("returns 200 and JWT token when credentials are valid")
        void shouldLoginSuccessfully() throws Exception {
            LoginRequest req = new LoginRequest("user@test.com", "password123");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isString())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("returns 401 when password is wrong")
        void shouldReturnUnauthorizedWhenPasswordIsWrong() throws Exception {
            LoginRequest req = new LoginRequest("user@test.com", "wrongpassword");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 401 when user does not exist")
        void shouldReturnUnauthorizedWhenUserNotFound() throws Exception {
            LoginRequest req = new LoginRequest("ghost@test.com", "password123");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }
        @Test
        @DisplayName("returns 429 when rate limit is exceeded")
        void shouldReturnTooManyRequestsWhenRateLimitExceeded() throws Exception {
            LoginRequest req = new LoginRequest("user@test.com", "wrongpassword");

            for (int i = 0; i < 5; i++) {
                mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)));
            }

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isTooManyRequests());
        }


        @Test
        @DisplayName("can login with CPF as credential")
        void shouldLoginWithCpf() throws Exception {
            LoginRequest req = new LoginRequest("52998224725", "password123");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("returns 400 when body is missing")
        void shouldReturnBadRequestWhenBodyIsMissing() throws Exception {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /auth/register/admin")
    class RegisterAdmin {

        @Test
        @DisplayName("returns 403 when requester is a regular user")
        void shouldReturnForbiddenWhenRequesterIsUser() throws Exception {
            RegisterRequest req = new RegisterRequest(
                    "Fake", "Admin", "fakeadmin@test.com", "password123",
                    "98058100030", "11987654321",
                    LocalDate.of(1990, 1, 1), Gender.MALE
            );

            mockMvc.perform(post("/auth/register/admin")
                            .header("Authorization", bearer(userToken()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("returns 401 when no token is provided")
        void shouldReturnUnauthorizedWhenNoToken() throws Exception {
            RegisterRequest req = new RegisterRequest(
                    "No", "Auth", "noauth@test.com", "password123",
                    "871.943.710-89", "11987654321",
                    LocalDate.of(1990, 1, 1), Gender.MALE
            );

            mockMvc.perform(post("/auth/register/admin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }
    }
}