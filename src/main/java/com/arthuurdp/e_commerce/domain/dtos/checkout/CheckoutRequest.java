package com.arthuurdp.e_commerce.domain.dtos.checkout;

import com.arthuurdp.e_commerce.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull(message = "Address is required")
        Long addressId,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {}