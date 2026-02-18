package com.arthuurdp.e_commerce.entities.dtos.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import jakarta.validation.constraints.*;

public record RegisterProductRequest(
        @NotBlank @Size(min = 2, max = 150)
        String name,

        @NotBlank @Size(max = 500)
        String description,

        @NotNull @DecimalMin(value = "0.0", inclusive = true)
        Double price,

        @NotNull @Min(0)
        Integer stock,

        @NotEmpty
        List<@NotBlank String> images,

        @NotEmpty
        List<@NotNull Long> categoryIds

) {}

