package com.arthuurdp.e_commerce.entities.dtos.carrier;

import com.arthuurdp.e_commerce.entities.dtos.places.StateResponse;
import com.arthuurdp.e_commerce.entities.enums.CarrierStatus;

public record CarrierResponse(Long id, String name, CarrierStatus status, StateResponse state) {
}
