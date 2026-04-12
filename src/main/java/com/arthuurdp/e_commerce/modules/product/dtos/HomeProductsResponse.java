package com.arthuurdp.e_commerce.modules.product.dtos;

import com.arthuurdp.e_commerce.modules.category.dtos.CategoryResponse;

import java.util.List;

public record HomeProductsResponse(
        CategoryResponse category,
        List<ProductResponse> products
) {}
