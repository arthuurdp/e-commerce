package com.arthuurdp.e_commerce.domain.dtos.product;

public record ProductImageResponse(
        Long id,
        String url,
        boolean mainImage
) {}
