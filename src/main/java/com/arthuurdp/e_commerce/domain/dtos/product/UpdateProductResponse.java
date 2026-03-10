package com.arthuurdp.e_commerce.domain.dtos.product;

import com.arthuurdp.e_commerce.domain.dtos.category.CategoryResponse;

import java.time.Instant;
import java.util.List;

public record UpdateProductResponse(
        Long id,
        String name,
        String description,
        java.math.BigDecimal price,
        Integer stock,
        Double weight,
        Integer width,
        Integer height,
        Integer length,
        List<ProductImageResponse> imgs,
        List<CategoryResponse> category,
        Instant updatedAt
) {
}
