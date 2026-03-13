package com.arthuurdp.e_commerce.modules.product.dtos;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductRequest(
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Double weight,
        Integer width,
        Integer height,
        Integer length,
        Long mainImageId,
        List<String> imageUrls,
        List<Long> categoryIds
) {}


