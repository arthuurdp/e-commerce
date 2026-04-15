package com.arthuurdp.e_commerce.modules.review.dtos;

import com.arthuurdp.e_commerce.modules.comment.dtos.CommentResponse;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long userId,
        String userName,
        Long productId,
        Integer rating,
        CommentResponse comment,
        LocalDateTime createdAt
) {}
