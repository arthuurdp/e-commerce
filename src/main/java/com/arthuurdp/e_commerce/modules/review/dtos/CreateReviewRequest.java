package com.arthuurdp.e_commerce.modules.review.dtos;

import com.arthuurdp.e_commerce.modules.comment.dtos.CreateCommentRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateReviewRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        Integer rating,

        CreateCommentRequest comment
) {}
