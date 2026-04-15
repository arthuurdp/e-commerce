package com.arthuurdp.e_commerce.modules.comment.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotBlank(message = "Comment content cannot be empty")
        String content
) {}
