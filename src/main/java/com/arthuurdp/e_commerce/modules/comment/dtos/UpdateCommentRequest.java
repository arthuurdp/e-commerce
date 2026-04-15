package com.arthuurdp.e_commerce.modules.comment.dtos;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentRequest(
        @NotBlank(message = "Comment content cannot be empty")
        String content
) {}
