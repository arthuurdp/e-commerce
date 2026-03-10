package com.arthuurdp.e_commerce.domain.dtos.email;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "Password is required")
        String password
) {}
