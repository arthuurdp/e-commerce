package com.arthuurdp.e_commerce.modules.review;

import com.arthuurdp.e_commerce.modules.comment.CommentRepository;
import com.arthuurdp.e_commerce.modules.comment.entity.Comment;
import com.arthuurdp.e_commerce.modules.notification.NotificationService;
import com.arthuurdp.e_commerce.modules.product.ProductRepository;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.modules.review.dtos.CreateReviewRequest;
import com.arthuurdp.e_commerce.modules.review.dtos.ReviewResponse;
import com.arthuurdp.e_commerce.modules.review.dtos.UpdateReviewRequest;
import com.arthuurdp.e_commerce.modules.review.entity.Review;
import com.arthuurdp.e_commerce.modules.review.mapper.ReviewMapper;
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
    private final CommentRepository commentRepository;
    private final ReviewMapper mapper;
    private final NotificationService notificationService;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository, CommentRepository commentRepository, ReviewMapper mapper, NotificationService notificationService) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.commentRepository = commentRepository;
        this.mapper = mapper;
        this.notificationService = notificationService;
    }

    @Transactional
    public ReviewResponse createReview(Long productId, CreateReviewRequest req, User user) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (reviewRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new ConflictException("You have already reviewed this product");
        }

        Review review = reviewRepository.save(new Review(user, product, req.rating()));
        notificationService.createNotification(user, "You have added a review to " + product.getName() + "!", "REVIEW");

        if (req.comment() != null) {
            Comment comment = new Comment(user, product, req.comment().content());
            review.setComment(comment);
            commentRepository.save(comment);
            notificationService.createNotification(user, "You have added a comment in your review!", "COMMENT");
        }

        return mapper.toReviewResponse(review);
    }
    @Transactional
    public ReviewResponse updateReview(Long reviewId, UpdateReviewRequest req, User user) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own reviews");
        }

        review.setRating(req.rating());

        return mapper.toReviewResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own reviews");
        }

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getUserReviews(User user) {
        return mapper.toReviewResponseList(reviewRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }
    @Transactional(readOnly = true)
    public List<ReviewResponse> getProductReviews(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }
        return mapper.toReviewResponseList(reviewRepository.findByProductId(productId));
    }

    public Double getAverageRating(Long productId) {
        Double avg = reviewRepository.getAverageRatingByProductId(productId);
        return avg != null ? avg : 0.0;
    }

    public Long getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }
}
