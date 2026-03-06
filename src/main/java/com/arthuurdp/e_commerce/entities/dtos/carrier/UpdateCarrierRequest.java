package com.arthuurdp.e_commerce.entities.dtos.carrier;

import jakarta.validation.constraints.NotBlank;

public record UpdateCarrierRequest(
        @NotBlank(message = "Carrier name cannot be blank")
        String name
) {
}
