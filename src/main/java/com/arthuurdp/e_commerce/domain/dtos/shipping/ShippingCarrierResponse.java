package com.arthuurdp.e_commerce.domain.dtos.shipping;

import com.arthuurdp.e_commerce.domain.dtos.carrier.CarrierResponse;
import com.arthuurdp.e_commerce.domain.enums.Region;
import com.arthuurdp.e_commerce.domain.enums.ShippingCarrierStatus;

public record ShippingCarrierResponse(
        Long id,
        Long shippingId,
        CarrierResponse carrier,
        Region region,
        Integer legOrder,
        ShippingCarrierStatus status
) {
}
