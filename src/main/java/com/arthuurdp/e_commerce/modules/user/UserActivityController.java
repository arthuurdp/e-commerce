package com.arthuurdp.e_commerce.modules.user;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import com.arthuurdp.e_commerce.modules.comment.dtos.CommentResponse;
import com.arthuurdp.e_commerce.modules.myactivity.MyActivityService;
import com.arthuurdp.e_commerce.modules.notification.dtos.NotificationResponse;
import com.arthuurdp.e_commerce.modules.product.dtos.ProductResponse;
import com.arthuurdp.e_commerce.modules.review.dtos.ReviewResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users/me/activity")
public class UserActivityController {
    private final MyActivityService service;

    public UserActivityController(MyActivityService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<NotificationResponse>> getRecentActivity(
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(service.getRecentActivity(authenticatedUser.getUser()));
    }

    @GetMapping("/reviews")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(service.getUserReviews(authenticatedUser.getUser()));
    }

    @GetMapping("/comments")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CommentResponse>> getMyComments(
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(service.getUserComments(authenticatedUser.getUser()));
    }

    @GetMapping("/favorites")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Set<ProductResponse>> getMyFavorites(
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(service.getUserFavorites(authenticatedUser.getUser()));
    }
}
