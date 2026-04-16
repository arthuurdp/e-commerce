package com.arthuurdp.e_commerce.modules.comment;

import com.arthuurdp.e_commerce.modules.comment.dtos.CommentResponse;
import com.arthuurdp.e_commerce.modules.comment.dtos.UpdateCommentRequest;
import com.arthuurdp.e_commerce.modules.comment.entity.Comment;
import com.arthuurdp.e_commerce.modules.comment.mapper.CommentMapper;
import com.arthuurdp.e_commerce.modules.notification.NotificationService;
import com.arthuurdp.e_commerce.modules.product.ProductRepository;
import com.arthuurdp.e_commerce.modules.product.dtos.ProductResponse;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.modules.review.ReviewRepository;
import com.arthuurdp.e_commerce.modules.review.dtos.AddCommentToReviewRequest;
import com.arthuurdp.e_commerce.modules.review.entity.Review;
import com.arthuurdp.e_commerce.modules.user.UserRepository;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.shared.exceptions.AccessDeniedException;
import com.arthuurdp.e_commerce.shared.exceptions.ConflictException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final CommentMapper mapper;
    private final NotificationService notificationService;

    public CommentService(CommentRepository commentRepository, ProductRepository productRepository, ReviewRepository reviewRepository, CommentMapper mapper, NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.mapper = mapper;
        this.notificationService = notificationService;
    }

    @Transactional
    public CommentResponse addCommentToReview(Long reviewId, AddCommentToReviewRequest req, User user) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        Product product = review.getProduct();

        if (!review.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only comment on your own reviews");
        }

        if (commentRepository.existsByReviewId(review.getId())) {
            throw new ConflictException("This review already has a comment");
        }

        Comment comment = new Comment(user, product, req.content());
        comment.setReview(review);

        Comment savedComment = commentRepository.save(comment);
        notificationService.createNotification(user, "You have added a comment in your review!");

        return mapper.toCommentResponseDTO(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, UpdateCommentRequest req, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own comments");
        }

        comment.setContent(req.content());
        return mapper.toCommentResponseDTO(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId()) && !user.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getUserComments(User user) {
        return mapper.toCommentResponseDTOList(commentRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getProductComments(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }
        return mapper.toCommentResponseDTOList(commentRepository.findByProductId(productId));
    }
}
