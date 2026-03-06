package com.arthuurdp.e_commerce.entities.dtos.shipping;

import com.arthuurdp.e_commerce.entities.dtos.carrier.CarrierResponse;
import com.arthuurdp.e_commerce.entities.dtos.places.StateResponse;
import com.arthuurdp.e_commerce.entities.enums.ShippingCarrierStatus;

public record ShippingCarrierResponse(
        Long id,
        Long shippingId,
        CarrierResponse carrier,
        StateResponse state,
        Integer legOrder,
        ShippingCarrierStatus status
) {
}
