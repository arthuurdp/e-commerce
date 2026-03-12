package com.arthuurdp.e_commerce.modules.order.dtos;

import com.arthuurdp.e_commerce.modules.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal total,
        Integer totalItems,
        LocalDateTime createdAt
) {}
