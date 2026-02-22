package com.arthuurdp.e_commerce.entities.dtos.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateAddressRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Street is required")
        String street,

        @NotNull(message = "Number is required")
        @Positive(message = "Number must be greater than zero")
        Integer number,

        @NotBlank(message = "Complement is required")
        String complement,

        @NotBlank(message = "Neighborhood is required")
        String neighborhood,

        @NotNull(message = "State is required")
        Long stateId,

        @NotNull(message = "City is required")
        Long cityId
) {}
