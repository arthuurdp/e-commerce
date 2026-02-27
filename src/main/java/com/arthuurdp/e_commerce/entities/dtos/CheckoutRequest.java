package com.arthuurdp.e_commerce.entities.dtos;

import com.arthuurdp.e_commerce.entities.enums.PaymentMethod;

import java.util.List;

public record CheckoutRequest(
        List<OrderItemDTO> items,
        String payerEmail,
        PaymentMethod paymentMethod,
        Long orderId
) {
}
