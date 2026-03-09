package com.arthuurdp.e_commerce.domain.dtos.shipping;

import com.arthuurdp.e_commerce.domain.enums.ShippingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShippingResponse(
        Long id,
        Long orderId,
        ShippingStatus status,
        String carrier,
        String trackingCode,
        String trackingUrl,
        BigDecimal shippingCost,
        LocalDateTime postedAt,
        LocalDateTime deliveredAt,
        LocalDateTime createdAt
) {}