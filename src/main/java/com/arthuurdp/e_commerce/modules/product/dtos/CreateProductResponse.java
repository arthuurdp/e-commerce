package com.arthuurdp.e_commerce.modules.product.dtos;

import com.arthuurdp.e_commerce.modules.category.dtos.CategoryResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Double weight,
        Integer width,
        Integer height,
        Integer length,
        List<ProductImageResponse> imgs,
        List<CategoryResponse> categories,
        LocalDateTime createdAt
) {}
