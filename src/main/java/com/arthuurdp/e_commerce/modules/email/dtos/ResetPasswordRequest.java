package com.arthuurdp.e_commerce.modules.email.dtos;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "Code is required")
        String code,

        @NotBlank(message = "New password is required")
        String newPassword
) {}
