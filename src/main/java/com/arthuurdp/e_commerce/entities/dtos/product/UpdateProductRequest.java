package com.arthuurdp.e_commerce.entities.dtos.product;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductRequest(String name, String description, BigDecimal price, Integer stock, String mainImageUrl, List<String> imageUrls, List<Long> categoryIds) {}


