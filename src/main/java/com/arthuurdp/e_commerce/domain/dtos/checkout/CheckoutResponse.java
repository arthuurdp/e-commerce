package com.arthuurdp.e_commerce.domain.dtos.checkout;

public record CheckoutResponse(
        Long orderId,
        String preferenceId,
        String initPoint
) {}