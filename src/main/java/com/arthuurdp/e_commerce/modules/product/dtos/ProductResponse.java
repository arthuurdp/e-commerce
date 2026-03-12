package com.arthuurdp.e_commerce.modules.product.dtos;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String mainImage
) {}
