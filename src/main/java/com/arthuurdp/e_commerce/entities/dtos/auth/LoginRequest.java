package com.arthuurdp.e_commerce.entities.dtos.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Please enter your credential")
        String credential,

        @NotBlank(message = "Password is required")
        String password
) {
}