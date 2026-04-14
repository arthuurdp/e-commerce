package com.arthuurdp.e_commerce.modules.review.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long productId;
    private Integer rating;
    private LocalDateTime createdAt;
}