package com.arthuurdp.e_commerce.modules.order.dtos;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        String productMainImage,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}
