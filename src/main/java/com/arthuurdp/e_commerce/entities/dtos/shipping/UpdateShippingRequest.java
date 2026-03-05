package com.arthuurdp.e_commerce.entities.dtos.shipping;

import com.arthuurdp.e_commerce.entities.enums.ShippingStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateShippingRequest(
        @NotNull(message = "Status is required")
        ShippingStatus status,

        String carrier,
        String trackingCode
) {}