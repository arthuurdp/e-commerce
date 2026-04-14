package com.arthuurdp.e_commerce.modules.comment.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCommentDTO {
    @NotBlank(message = "Comment content cannot be empty")
    private String content;
}
