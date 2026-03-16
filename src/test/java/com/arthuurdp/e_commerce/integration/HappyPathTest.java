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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Happy Path Test")
public class HappyPathTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("POST /auth/register")
    class Register {

        @Test
        @DisplayName("returns 201 and user data when request is valid")
        void shouldRegisterSuccessfully() throws Exception {
            RegisterRequest req = new RegisterRequest(
                    "User", "Test", "usertest@gmail.com", "test123",
                    "39209275080", "11987654321",
                    LocalDate.of(1995, 6, 15), Gender.MALE
            );

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value("usertest@gmail.com"))
                    .andExpect(jsonPath("$.firstName").value("User"))
                    .andExpect(jsonPath("$.id").isNumber());
        }
    }

    @Nested
    @DisplayName("POST /auth/login")
    class Login {

        @Test
        @DisplayName("returns 200 and token when credentials are valid")
        void shouldLoginSuccessfully() throws Exception {
            LoginRequest req = new LoginRequest("usertest@gmail.com", "test123");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isString());
        }
    }
}
