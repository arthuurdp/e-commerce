package com.arthuurdp.e_commerce.entities.dtos.checkout;

import com.arthuurdp.e_commerce.entities.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull(message = "Address is required")
        Long addressId,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {}
