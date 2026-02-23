package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.cart.CartItemResponse;
import com.arthuurdp.e_commerce.entities.dtos.cart.CartResponse;
import com.arthuurdp.e_commerce.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/cart")
public class CartController {
    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<CartResponse> display() {
        return ResponseEntity.ok().body(service.display());
    }

    @PatchMapping("/{productId}/increment")
    public ResponseEntity<CartItemResponse> addProduct(@PathVariable Long productId) {
        return ResponseEntity.ok().body(service.addProduct(productId));
    }

    @PatchMapping("/{productId}/decrement")
    public ResponseEntity<CartItemResponse> removeProduct(@PathVariable Long productId) {
        return service.removeProduct(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<Void> clear() {
        service.clear();
        return ResponseEntity.noContent().build();
    }
}
