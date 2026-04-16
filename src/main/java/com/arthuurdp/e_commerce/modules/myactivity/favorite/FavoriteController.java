package com.arthuurdp.e_commerce.modules.myactivity.favorite;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users/me/activity/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> addFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(favoriteService.addFavorite(productId, authenticatedUser.getUser()));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        favoriteService.removeFavorite(productId, authenticatedUser.getUser());
        return ResponseEntity.noContent().build();
    }
}