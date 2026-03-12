package com.arthuurdp.e_commerce.modules.email.dtos;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "Password is required")
        String password
) {}
