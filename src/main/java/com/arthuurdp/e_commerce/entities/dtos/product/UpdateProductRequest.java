package com.arthuurdp.e_commerce.entities.dtos.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductRequest(
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 chars")
        String name,

        @Size(max = 200, message = "Description must be under 200 chars")
        String description,

        @Positive(message = "Price must be greater than zero")
        BigDecimal price,

        @Min(value = 1, message = "Stock must be 1 or greater")
        Integer stock,

        List<@NotBlank(message = "Image cannot be blank") String> images,

        List<@NotNull(message = "Category id cannot be null") Long> categoryIds
) {}


