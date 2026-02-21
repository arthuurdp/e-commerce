package com.arthuurdp.e_commerce.entities.dtos.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateAddressRequest(
        @NotBlank(message = "Street is required")
        String street,

        @NotNull(message = "Number is required")
        @Positive(message = "Number must be greater than zero")
        Integer number,

        @NotBlank(message = "Neighborhood is required")
        String neighborhood,

        @NotNull(message = "City id is required")
        @Positive(message = "City id must be greater than zero")
        Long cityId
) {}
