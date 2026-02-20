package com.arthuurdp.e_commerce.entities.dtos.cart_item;

import java.math.BigDecimal;

public record CartItemResponse(Long id, String name, BigDecimal price, Integer quantity, BigDecimal subtotal) {
}
