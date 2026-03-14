package com.arthuurdp.e_commerce.modules.cart;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import com.arthuurdp.e_commerce.modules.cart.dtos.CartItemResponse;
import com.arthuurdp.e_commerce.modules.cart.dtos.CartResponse;
import com.arthuurdp.e_commerce.modules.user.entity.User;
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
    public ResponseEntity<CartResponse> display(
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok().body(service.display(authenticatedUser.getUser()));
    }

    @PatchMapping("/{productId}/increment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartItemResponse> addProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok().body(service.addProduct(productId, authenticatedUser.getUser()));
    }

    @PatchMapping("/{productId}/decrement")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartItemResponse> removeProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return service.removeProduct(productId, authenticatedUser.getUser())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> clear(
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        service.clear(authenticatedUser.getUser());
        return ResponseEntity.noContent().build();
    }
}
