package com.arthuurdp.e_commerce.entities.dtos;

import java.time.Instant;
import java.util.List;

public record UpdateProductResponse(Long id, String name, String description, Double price, Integer stock, List<CategoryResponse> category, Instant updatedAt) {
}
