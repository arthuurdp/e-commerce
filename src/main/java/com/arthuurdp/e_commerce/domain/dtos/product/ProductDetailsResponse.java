package com.arthuurdp.e_commerce.domain.dtos.product;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailsResponse(Long id, String name, String description, BigDecimal price, List<ProductImageResponse> imgs) {
}
