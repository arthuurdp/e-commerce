package com.arthuurdp.e_commerce.entities.dtos.address;

import com.arthuurdp.e_commerce.entities.dtos.places.CityResponse;
import com.arthuurdp.e_commerce.entities.dtos.places.StateResponse;

public record AddressResponse(Long id, String name, String street, Integer number, String complement, String neighborhood, CityResponse city, StateResponse state) {}
