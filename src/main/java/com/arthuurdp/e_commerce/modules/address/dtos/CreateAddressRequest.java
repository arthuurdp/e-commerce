package com.arthuurdp.e_commerce.modules.address.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateAddressRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Street is required")
        String street,

        @NotNull(message = "Number is required")
        @Positive(message = "Number must be greater than zero")
        Integer number,

        String complement,

        @NotBlank(message = "Neighborhood is required")
        String neighborhood,

        @NotBlank(message = "Postal code is required")
        @Size(min = 8, max = 9, message = "Invalid postal code")
        String postalCode
) {}