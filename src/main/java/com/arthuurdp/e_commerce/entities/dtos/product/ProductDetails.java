package com.arthuurdp.e_commerce.entities.dtos.product;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetails(Long id, String name, String description, BigDecimal price, List<ProductImageResponse> imgs) {
}
