package com.arthuurdp.e_commerce.entities.dtos.email;

import jakarta.validation.constraints.NotBlank;

public record ConfirmEmailChangeRequest(
        @NotBlank(message = "Code not valid")
        String code
) {}
