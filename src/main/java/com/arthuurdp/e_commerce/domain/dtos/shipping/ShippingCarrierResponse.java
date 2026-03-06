package com.arthuurdp.e_commerce.domain.dtos.shipping;

import com.arthuurdp.e_commerce.domain.dtos.carrier.CarrierResponse;
import com.arthuurdp.e_commerce.domain.dtos.places.StateResponse;
import com.arthuurdp.e_commerce.domain.enums.ShippingCarrierStatus;

public record ShippingCarrierResponse(
        Long id,
        Long shippingId,
        CarrierResponse carrier,
        StateResponse state,
        Integer legOrder,
        ShippingCarrierStatus status
) {
}
