package com.arthuurdp.e_commerce.entities.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must have at most 50 chars")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must have at most 50 chars")
        String lastName,

        @NotBlank(message = "E-mail is required")
        @Email(message = "Please enter a valid e-mail")
        @Size(max = 100, message = "Email must have at most 100 chars")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must have at least 6 chars")
        String password
) {}

