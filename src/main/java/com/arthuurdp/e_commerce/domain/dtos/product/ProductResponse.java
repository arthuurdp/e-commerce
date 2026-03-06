package com.arthuurdp.e_commerce.domain.dtos.product;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description, BigDecimal price, String mainImage) {
}
