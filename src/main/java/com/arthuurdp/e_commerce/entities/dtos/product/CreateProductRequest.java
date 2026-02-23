package com.arthuurdp.e_commerce.entities.dtos.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 chars")
        String name,

        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Description must be under 500 chars")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,

        @NotNull(message = "Stock is required")
        @Min(value = 1, message = "Stock must be 1 or greater")
        Integer stock,

        @NotEmpty(message = "Images are required")
        List<@NotBlank(message = "Image URL cannot be blank") String> images,

        String mainImageUrl,

        @NotEmpty(message = "Categories are required")
        List<@NotNull(message = "Category cannot be null") Long> categoryIds
) {}

