package com.arthuurdp.e_commerce.modules.cart.dtos;

import java.math.BigDecimal;

public record CartItemResponse(
        Long productId,
        String imageUrl,
        String name,
        BigDecimal price,
        Integer quantity,
        BigDecimal subtotal
) {}
