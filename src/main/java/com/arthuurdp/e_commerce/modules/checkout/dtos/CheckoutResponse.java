package com.arthuurdp.e_commerce.modules.checkout.dtos;

public record CheckoutResponse(
        Long orderId,
        String sessionId,
        String checkoutUrl
) {}