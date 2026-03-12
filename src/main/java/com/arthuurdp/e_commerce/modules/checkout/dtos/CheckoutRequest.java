package com.arthuurdp.e_commerce.modules.checkout.dtos;

import com.arthuurdp.e_commerce.modules.payment.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull(message = "Address is required")
        Long addressId,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {}