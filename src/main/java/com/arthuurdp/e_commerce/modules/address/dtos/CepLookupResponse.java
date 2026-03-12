package com.arthuurdp.e_commerce.modules.address.dtos;

public record CepLookupResponse(
        String cep,
        String street,
        String neighborhood,
        Long cityId,
        String cityName,
        Long stateId,
        String stateName,
        String stateUf
) {}