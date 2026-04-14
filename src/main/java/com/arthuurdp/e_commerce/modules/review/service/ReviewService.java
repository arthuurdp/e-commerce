package com.arthuurdp.e_commerce.modules.review.service;

import com.arthuurdp.e_commerce.modules.product.ProductRepository;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.modules.review.dtos.CreateReviewRequest;
import com.arthuurdp.e_commerce.modules.review.dtos.ReviewResponse;
import com.arthuurdp.e_commerce.modules.review.dtos.UpdateReviewRequest;
import com.arthuurdp.e_commerce.modules.review.entity.Review;
import com.arthuurdp.e_commerce.modules.review.repository.ReviewRepository;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.shared.exceptions.AccessDeniedException;
import com.arthuurdp.e_commerce.shared.exceptions.ConflictException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest createReviewRequest, User user) {
        Product product = productRepository.findById(createReviewRequest.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (reviewRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new ConflictException("You have already reviewed this product");
        }

        Review review = new Review(user, product, createReviewRequest.getRating());
        Review savedReview = reviewRepository.save(review);

        return mapToResponseDTO(savedReview);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, UpdateReviewRequest updateReviewRequest, User user) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own reviews");
        }

        review.setRating(updateReviewRequest.getRating());

        Review updatedReview = reviewRepository.save(review);
        return mapToResponseDTO(updatedReview);
    }

    public List<ReviewResponse> getProductReviews(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }
        return reviewRepository.findByProductId(productId).stream().map(this::mapToResponseDTO).toList();
    }

    public Double getAverageRating(Long productId) {
        Double avg = reviewRepository.getAverageRatingByProductId(productId);
        return avg != null ? avg : 0.0;
    }

    public Long getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    private ReviewResponse mapToResponseDTO(Review review) {
        ReviewResponse dto = new ReviewResponse();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getFirstName() + " " + review.getUser().getLastName());
        dto.setProductId(review.getProduct().getId());
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}