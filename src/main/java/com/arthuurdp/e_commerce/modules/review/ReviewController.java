package com.arthuurdp.e_commerce.modules.review;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import com.arthuurdp.e_commerce.modules.review.dtos.CreateReviewRequest;
import com.arthuurdp.e_commerce.modules.review.dtos.ReviewResponse;
import com.arthuurdp.e_commerce.modules.review.dtos.UpdateReviewRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody CreateReviewRequest createReviewRequest,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(reviewService.createReview(createReviewRequest, authenticatedUser.getUser()));
    }

    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest updateReviewRequest,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, updateReviewRequest, authenticatedUser.getUser()));
    }

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    @GetMapping("/{productId}/rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getAverageRating(productId));
    }
}