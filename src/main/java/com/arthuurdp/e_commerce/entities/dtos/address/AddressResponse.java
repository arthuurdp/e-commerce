package com.arthuurdp.e_commerce.entities.dtos.address;

import com.arthuurdp.e_commerce.entities.enums.StateUF;

public record AddressResponse(Long id, String street, Integer number, String neighborhood, Long cityId, String cityName, StateUF state) {}
