package com.arthuurdp.e_commerce.entities.dtos.product;

import jakarta.validation.constraints.*;

import java.util.List;

public record UpdateProductRequest(

        @Size(min = 2, max = 100)
        String name,

        @Size(max = 200)
        String description,

        @Positive
        Double price,

        @Min(0)
        Integer stock,

        List<@NotBlank String> images,

        List<@NotNull Long> categoryIds
) {}


