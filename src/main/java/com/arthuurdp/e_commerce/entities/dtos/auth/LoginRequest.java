package com.arthuurdp.e_commerce.entities.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "E-mail is required")
        @Email(message = "Please enter a valid e-mail")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password has to be at least 6 chars")
        String password) {
}