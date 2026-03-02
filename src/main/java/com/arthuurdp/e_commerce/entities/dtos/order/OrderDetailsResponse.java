package com.arthuurdp.e_commerce.entities.dtos.order;

import com.arthuurdp.e_commerce.entities.dtos.order_item.OrderItemResponse;
import com.arthuurdp.e_commerce.entities.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailsResponse(
        Long id,
        OrderStatus status,
        BigDecimal total,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
}
