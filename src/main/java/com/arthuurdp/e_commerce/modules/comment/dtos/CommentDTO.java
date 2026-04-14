package com.arthuurdp.e_commerce.modules.comment.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentDTO {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Comment content cannot be empty")
    private String content;
}
