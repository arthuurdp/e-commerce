package com.arthuurdp.e_commerce.modules.product.dtos;

import com.arthuurdp.e_commerce.modules.category.dtos.CategoryResponse;

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
        List<CategoryResponse> categories,
        Instant updatedAt
) {}
