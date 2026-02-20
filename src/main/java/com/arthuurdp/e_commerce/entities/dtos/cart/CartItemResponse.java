package com.arthuurdp.e_commerce.entities.dtos.cart;

import java.math.BigDecimal;

public record CartItemResponse(Long productId, String imageUrl, String name, BigDecimal price, Integer quantity, BigDecimal subtotal) {
}
