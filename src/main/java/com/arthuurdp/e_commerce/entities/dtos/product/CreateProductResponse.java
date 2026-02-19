package com.arthuurdp.e_commerce.entities.dtos.product;

import com.arthuurdp.e_commerce.entities.dtos.category.CategoryResponse;

import java.time.Instant;
import java.util.List;

public record CreateProductResponse(Long id, String name, String description, Double price, Integer stock, List<CategoryResponse> categories, Instant createdAt) {
}
