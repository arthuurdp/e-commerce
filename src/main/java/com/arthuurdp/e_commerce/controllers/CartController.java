package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.domain.dtos.cart.CartItemResponse;
import com.arthuurdp.e_commerce.domain.dtos.cart.CartResponse;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> display(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(service.display(user));
    }

    @PatchMapping("/{productId}/increment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartItemResponse> addProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok().body(service.addProduct(productId, user));
    }

    @PatchMapping("/{productId}/decrement")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartItemResponse> removeProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal User user
    ) {
        return service.removeProduct(productId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> clear(User user) {
        service.clear(user);
        return ResponseEntity.noContent().build();
    }
}
