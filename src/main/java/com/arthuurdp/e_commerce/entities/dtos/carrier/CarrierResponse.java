package com.arthuurdp.e_commerce.entities.dtos.carrier;

import com.arthuurdp.e_commerce.entities.dtos.places.StateResponse;
import com.arthuurdp.e_commerce.entities.enums.ActiveCarrierStatus;

public record CarrierResponse(Long id, String name, ActiveCarrierStatus status, StateResponse state) {
}
