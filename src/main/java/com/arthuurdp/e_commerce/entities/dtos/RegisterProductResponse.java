package com.arthuurdp.e_commerce.entities.dtos;

import java.time.Instant;
import java.util.List;

public record RegisterProductResponse(Long id, String name, String description, Double price, Integer stock, List<CategoryResponse> categories, Instant createdAt) {
}
