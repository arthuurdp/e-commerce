package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.User;
import com.arthuurdp.e_commerce.entities.dtos.cart_item.CartItemResponse;
import com.arthuurdp.e_commerce.services.AuthService;
import com.arthuurdp.e_commerce.services.CartItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
public class CartController {
    private final CartItemService cartItemService;
    private final AuthService authService;

    public CartController(CartItemService cartItemService, AuthService authService) {
        this.cartItemService = cartItemService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<Page<CartItemResponse>> findAllItems(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok().body(cartItemService.findAllItems(user.getCart().getId(), page, size));
    }

    @PatchMapping("/{productId}/increment")
    public ResponseEntity<CartItemResponse> addProduct(@PathVariable Long productId) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok().body(cartItemService.addItem(user.getCart().getId(), productId));
    }

    @PatchMapping("/{productId}/decrement")
    public ResponseEntity<CartItemResponse> removeProduct(@PathVariable Long productId) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok().body(cartItemService.removeItem(user.getCart().getId(), productId));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeAllItems() {
        User user = authService.getCurrentUser();
        cartItemService.removeAllItems(user.getCart().getId());
        return ResponseEntity.noContent().build();
    }
}
