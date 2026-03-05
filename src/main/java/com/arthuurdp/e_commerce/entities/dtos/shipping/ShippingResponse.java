package com.arthuurdp.e_commerce.entities.dtos.shipping;

import com.arthuurdp.e_commerce.entities.enums.ShippingStatus;

import java.time.LocalDateTime;

public record ShippingResponse(
        Long id,
        Long orderId,
        ShippingStatus status,
        String carrier,
        String trackingCode,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        LocalDateTime createdAt
) {}