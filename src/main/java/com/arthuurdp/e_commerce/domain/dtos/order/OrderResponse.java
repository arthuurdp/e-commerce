package com.arthuurdp.e_commerce.domain.dtos.order;

import com.arthuurdp.e_commerce.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal total,
        Integer totalItems,
        LocalDateTime createdAt
) {
}
