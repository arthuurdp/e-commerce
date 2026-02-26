package com.arthuurdp.e_commerce.entities.dtos.checkout;

import com.arthuurdp.e_commerce.entities.enums.OrderStatus;
import com.arthuurdp.e_commerce.entities.enums.PaymentMethod;
import com.arthuurdp.e_commerce.entities.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CheckoutResponse(
        Long orderId,
        OrderStatus orderStatus,
        BigDecimal total,
        LocalDateTime createdAt,

        Long paymentId,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,

        // Pix
        String pixQrCode,
        String pixQrCodeBase64,

        // Boleto
        String boletoUrl
) {
}
