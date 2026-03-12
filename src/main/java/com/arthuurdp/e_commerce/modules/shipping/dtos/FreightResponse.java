package com.arthuurdp.e_commerce.modules.shipping.dtos;

import java.math.BigDecimal;

public record FreightResponse(
        Integer serviceId,
        String name,
        BigDecimal price,
        Integer deliveryDays
) {}