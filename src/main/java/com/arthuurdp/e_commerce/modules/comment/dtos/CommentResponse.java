package com.arthuurdp.e_commerce.modules.comment.dtos;

import com.arthuurdp.e_commerce.modules.review.dtos.ReviewResponse;
import java.time.LocalDateTime;

public record CommentResponse (
    Long id,
    Long userId,
     String userName,
     ReviewResponse review,
     Long productId,
     String content,
     LocalDateTime createdAt
) {}
