package com.arthuurdp.e_commerce.domain.dtos.shipping;

import java.math.BigDecimal;

public record FreightResponse(
        Integer serviceId,
        String name,
        BigDecimal price,
        Integer deliveryDays
) {}