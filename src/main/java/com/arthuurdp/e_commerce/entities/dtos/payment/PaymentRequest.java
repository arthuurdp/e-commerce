package com.arthuurdp.e_commerce.entities.dtos.payment;

import com.arthuurdp.e_commerce.entities.enums.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        String description,
        String payerEmail,
        PaymentMethod paymentMethod
) {
}
