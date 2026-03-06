package com.arthuurdp.e_commerce.domain.dtos.carrier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCarrierRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "State is required")
        Long stateId
) {
}
