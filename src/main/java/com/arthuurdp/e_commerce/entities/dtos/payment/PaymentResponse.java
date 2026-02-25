package com.arthuurdp.e_commerce.entities.dtos.payment;

import com.arthuurdp.e_commerce.entities.enums.PaymentStatus;

public record PaymentResponse(
        Long id,
        PaymentStatus status,
        String statusDetail,
        String qrCode,
        String qrCodeBase64,
        String boletoUrl
) {
}
