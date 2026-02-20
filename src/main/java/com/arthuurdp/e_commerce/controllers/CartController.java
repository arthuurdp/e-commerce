package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.User;
import com.arthuurdp.e_commerce.entities.dtos.cart.CartItemResponse;
import com.arthuurdp.e_commerce.entities.dtos.cart.CartResponse;
import com.arthuurdp.e_commerce.services.AuthService;
import com.arthuurdp.e_commerce.services.CartService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
public class CartController {
    private final CartService cartService;
    private final AuthService authService;

    public CartController(CartService cartService, AuthService authService) {
        this.cartService = cartService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> displayCart() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok().body(cartService.findById(user.getCart().getId()));
    }

    @PatchMapping("/{productId}/increment")
    public ResponseEntity<CartItemResponse> addProduct(@PathVariable Long productId) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok().body(cartService.addItem(user.getCart().getId(), productId));
    }

    @PatchMapping("/{productId}/decrement")
    public ResponseEntity<CartItemResponse> removeProduct(@PathVariable Long productId) {
        User user = authService.getCurrentUser();
        return cartService.removeItem(user.getCart().getId(), productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<Void> removeAllItems() {
        User user = authService.getCurrentUser();
        cartService.removeAllItems(user.getCart().getId());
        return ResponseEntity.noContent().build();
    }
}
