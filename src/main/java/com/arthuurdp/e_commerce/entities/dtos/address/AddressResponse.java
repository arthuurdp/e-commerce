package com.arthuurdp.e_commerce.entities.dtos.address;

public record AddressResponse(Long id, String name, String street, Integer number, String complement, String neighborhood, CityResponse city, StateResponse state) {}
