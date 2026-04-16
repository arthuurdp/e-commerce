package com.arthuurdp.e_commerce.modules.review.dtos;

import jakarta.validation.constraints.NotBlank;

public record AddCommentToReviewRequest(
        @NotBlank(message = "Content is required")
        String content
) { }
