package com.arthuurdp.e_commerce.entities.dtos.checkout;

public record CheckoutResponse(
        Long orderId,
        String preferenceId,
        String sandboxInitPoint,
        String initPoint
) {}