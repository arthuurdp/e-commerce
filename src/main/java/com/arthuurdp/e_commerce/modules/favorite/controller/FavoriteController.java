package com.arthuurdp.e_commerce.modules.favorite.controller;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import com.arthuurdp.e_commerce.modules.favorite.service.FavoriteService;
import com.arthuurdp.e_commerce.modules.product.dtos.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/users/me/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        favoriteService.addFavorite(productId, authenticatedUser.getUser());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        favoriteService.removeFavorite(productId, authenticatedUser.getUser());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Set<ProductResponse>> getUserFavorites(
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(authenticatedUser.getUser()));
    }
}