package com.arthuurdp.e_commerce.entities.dtos.product;

import java.math.BigDecimal;

public record ProductDTO(Long id, String name, String description, BigDecimal price, String mainImage) {
}
