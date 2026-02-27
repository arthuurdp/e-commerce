package com.arthuurdp.e_commerce.entities.dtos;

public record CheckoutResponse(
        String preferenceId,
        String initPoint,
        String sandboxInitPoint
) {}
