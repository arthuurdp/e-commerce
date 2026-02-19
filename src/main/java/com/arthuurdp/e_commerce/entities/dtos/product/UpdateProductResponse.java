package com.arthuurdp.e_commerce.entities.dtos.product;

import com.arthuurdp.e_commerce.entities.ProductImage;
import com.arthuurdp.e_commerce.entities.dtos.category.CategoryResponse;

import java.time.Instant;
import java.util.List;

public record UpdateProductResponse(Long id, String name, String description, Double price, Integer stock, List<ProductImage> imgs, List<CategoryResponse> category, Instant updatedAt) {
}
