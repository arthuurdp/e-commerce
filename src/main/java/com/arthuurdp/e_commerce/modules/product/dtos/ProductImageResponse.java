package com.arthuurdp.e_commerce.modules.product.dtos;

public record ProductImageResponse(
        Long id,
        String url,
        boolean mainImage
) {}
