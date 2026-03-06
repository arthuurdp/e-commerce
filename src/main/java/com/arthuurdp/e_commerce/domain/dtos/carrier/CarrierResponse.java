package com.arthuurdp.e_commerce.domain.dtos.carrier;

import com.arthuurdp.e_commerce.domain.dtos.places.StateResponse;
import com.arthuurdp.e_commerce.domain.enums.CarrierStatus;

public record CarrierResponse(Long id, String name, CarrierStatus status, StateResponse state) {
}
