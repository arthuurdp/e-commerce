package com.arthuurdp.e_commerce.entities.dtos;

import com.arthuurdp.e_commerce.entities.enums.PaymentMethod;
import com.arthuurdp.e_commerce.entities.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(Long id, Long orderId, PaymentMethod method, PaymentStatus status, BigDecimal amount, LocalDateTime createdAt) {
}
