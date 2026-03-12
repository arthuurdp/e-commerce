package com.arthuurdp.e_commerce.modules.auth.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Please enter your credential")
        String credential,

        @NotBlank(message = "Password is required")
        String password
) {}