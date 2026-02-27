package com.arthuurdp.e_commerce.entities.dtos;

import java.math.BigDecimal;

public record OrderItemDTO(
        String title,
        int quantity,
        BigDecimal unitPrice,
        String currencyId  // "BRL"
) {}
