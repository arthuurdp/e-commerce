package com.arthuurdp.e_commerce.domain.dtos.address;

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