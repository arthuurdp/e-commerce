package com.arthuurdp.e_commerce.entities.dtos.product;

import com.arthuurdp.e_commerce.entities.dtos.category.CategoryDAO;

import java.time.Instant;
import java.util.List;

public record UpdateProductResponse(Long id, String name, String description, Double price, Integer stock, List<CategoryDAO> category, Instant updatedAt) {
}
