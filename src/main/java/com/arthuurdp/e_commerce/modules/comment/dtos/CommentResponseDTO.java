package com.arthuurdp.e_commerce.modules.comment.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long productId;
    private String content;
    private LocalDateTime createdAt;
}
