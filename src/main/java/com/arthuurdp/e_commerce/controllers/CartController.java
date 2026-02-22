package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.cart.CartItemResponse;
import com.arthuurdp.e_commerce.entities.dtos.cart.CartResponse;
import com.arthuurdp.e_commerce.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> displayCart() {
        return ResponseEntity.ok().body(cartService.findById());
    }

    @PatchMapping("/{productId}/increment")
    public ResponseEntity<CartItemResponse> addProduct(@PathVariable Long productId) {
        return ResponseEntity.ok().body(cartService.addProduct(productId));
    }

    @PatchMapping("/{productId}/decrement")
    public ResponseEntity<CartItemResponse> removeProduct(@PathVariable Long productId) {
        return cartService.removeProduct( productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<Void> removeAllItems() {
        cartService.removeAllItems();
        return ResponseEntity.noContent().build();
    }
}
