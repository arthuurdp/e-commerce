package com.arthuurdp.e_commerce.modules.address.dtos;

public record UpdateAddressRequest(
        String name,
        String street,
        Integer number,
        String complement,
        String neighborhood,
        String postalCode
) {}
