package com.arthuurdp.e_commerce.domain.dtos.carrier;

import com.arthuurdp.e_commerce.domain.enums.CarrierStatus;
import com.arthuurdp.e_commerce.domain.enums.Region;

public record CarrierResponse(Long id, String name, CarrierStatus status, Region region) {
}
