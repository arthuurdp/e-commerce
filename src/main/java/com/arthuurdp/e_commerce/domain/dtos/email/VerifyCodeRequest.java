package com.arthuurdp.e_commerce.domain.dtos.email;

import jakarta.validation.constraints.NotBlank;

public record VerifyCodeRequest(
        @NotBlank(message = "Code not valid")
        String code
) {
}
