package com.arthuurdp.e_commerce.domain.dtos.shipping;

import com.arthuurdp.e_commerce.domain.enums.ShippingStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ShippingResponse(
        Long id,
        Long orderId,
        ShippingStatus status,
        List<ShippingCarrierResponse> carriers,
        String trackingCode,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        LocalDateTime createdAt
) {}