package com.arthuurdp.e_commerce.modules.review.dtos;

import com.arthuurdp.e_commerce.modules.comment.dtos.CommentResponse;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long userId,
        Long productId,
        String productName,
        String userName,
        Integer rating,
        CommentResponse comment,
        String userProfilePictureUrl,
        LocalDateTime createdAt
) {}
