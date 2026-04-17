package com.arthuurdp.e_commerce.modules.review;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import com.arthuurdp.e_commerce.modules.comment.CommentService;
import com.arthuurdp.e_commerce.modules.comment.dtos.CommentResponse;
import com.arthuurdp.e_commerce.modules.review.dtos.AddCommentToReviewRequest;
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
    private final CommentService commentService;

    public ReviewController(ReviewService reviewService, CommentService commentService) {
        this.reviewService = reviewService;
        this.commentService = commentService;
    }

    @PostMapping("/{productId}/reviews")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest req,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(reviewService.createReview(productId, req, authenticatedUser.getUser()));
    }

    @PostMapping("/reviews/{reviewId}/comment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponse> addCommentToReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody AddCommentToReviewRequest req,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(commentService.addCommentToReview(reviewId, req, authenticatedUser.getUser()));
    }

    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest req,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, req, authenticatedUser.getUser()));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        reviewService.deleteReview(reviewId, authenticatedUser.getUser());
        return ResponseEntity.noContent().build();
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