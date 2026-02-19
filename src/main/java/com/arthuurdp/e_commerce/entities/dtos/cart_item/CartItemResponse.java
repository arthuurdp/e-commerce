package com.arthuurdp.e_commerce.entities.dtos.cart_item;

public record CartItemResponse(Long id, String name, Double price, Integer quantity, Double subtotal) {
}
