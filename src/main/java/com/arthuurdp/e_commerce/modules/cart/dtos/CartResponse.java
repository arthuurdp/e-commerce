package com.arthuurdp.e_commerce.modules.cart.dtos;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        Integer totalQuantity,
        List<CartItemResponse> items,
        BigDecimal total
) {}
