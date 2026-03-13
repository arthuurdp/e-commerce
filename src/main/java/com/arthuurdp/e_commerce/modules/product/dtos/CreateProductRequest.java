package com.arthuurdp.e_commerce.modules.product.dtos;

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

        @NotNull(message = "Weight is required")
        @Positive(message = "Weight must be positive")
        Double weight,

        @NotNull(message = "Width is required")
        @Positive(message = "Width must be positive")
        Integer width,

        @NotNull(message = "Height is required")
        @Positive(message = "Height must be positive")
        Integer height,

        @NotNull(message = "Length is required")
        @Positive(message = "Length must be positive")
        Integer length,

        @NotEmpty(message = "Images are required")
        List<@NotBlank(message = "Image URL cannot be blank") String> images,

        SetMainImageRequest mainImageRequest,

        @NotEmpty(message = "Categories are required")
        List<@NotNull(message = "Category cannot be null") Long> categoryIds
) {}

