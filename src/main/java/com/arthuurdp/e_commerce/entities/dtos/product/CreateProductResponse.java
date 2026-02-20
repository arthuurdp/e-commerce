package com.arthuurdp.e_commerce.entities.dtos.product;

import com.arthuurdp.e_commerce.entities.dtos.category.CategoryResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CreateProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        List<ProductImageResponse> imgs,
        List<CategoryResponse> categories,
        Instant createdAt
) {
}
