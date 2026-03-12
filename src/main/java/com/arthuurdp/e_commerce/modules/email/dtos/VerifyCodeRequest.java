package com.arthuurdp.e_commerce.modules.email.dtos;

import jakarta.validation.constraints.NotBlank;

public record VerifyCodeRequest(
        @NotBlank(message = "Code not valid")
        String code
) {}
