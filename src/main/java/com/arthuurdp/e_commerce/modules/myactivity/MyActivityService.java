package com.arthuurdp.e_commerce.modules.myactivity;

import com.arthuurdp.e_commerce.modules.comment.CommentService;
import com.arthuurdp.e_commerce.modules.comment.dtos.CommentResponse;
import com.arthuurdp.e_commerce.modules.myactivity.favorite.FavoriteService;
import com.arthuurdp.e_commerce.modules.notification.NotificationService;
import com.arthuurdp.e_commerce.modules.notification.dtos.NotificationResponse;
import com.arthuurdp.e_commerce.modules.product.dtos.ProductResponse;
import com.arthuurdp.e_commerce.modules.review.ReviewService;
import com.arthuurdp.e_commerce.modules.review.dtos.ReviewResponse;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class MyActivityService {
    private final NotificationService notificationService;
    private final ReviewService reviewService;
    private final CommentService commentService;
    private final FavoriteService favoriteService;

    public MyActivityService(
            NotificationService notificationService,
            ReviewService reviewService,
            CommentService commentService,
            FavoriteService favoriteService
    ) {
        this.notificationService = notificationService;
        this.reviewService = reviewService;
        this.commentService = commentService;
        this.favoriteService = favoriteService;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getRecentActivity(User user) {
        return notificationService.getMyNotifications(user);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getUserReviews(User user) {
        return reviewService.getUserReviews(user);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getUserComments(User user) {
        return commentService.getUserComments(user);
    }

    @Transactional(readOnly = true)
    public Set<ProductResponse> getUserFavorites(User user) {
        return favoriteService.getUserFavorites(user);
    }

    @Transactional
    public void clearRecentActivity(User user) {
        notificationService.clearRecentActivity(user);
    }
}
