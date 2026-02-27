package com.arthuurdp.e_commerce.entities.dtos;

public record OrderItemRequest(
        Long productId,
        Integer quantity
) {
}
