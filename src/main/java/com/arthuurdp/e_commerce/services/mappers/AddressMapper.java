package com.arthuurdp.e_commerce.services.mappers;

import com.arthuurdp.e_commerce.domain.dtos.address.AddressResponse;
import com.arthuurdp.e_commerce.domain.dtos.places.CityResponse;
import com.arthuurdp.e_commerce.domain.dtos.places.StateResponse;
import com.arthuurdp.e_commerce.domain.entities.Address;
import com.arthuurdp.e_commerce.domain.entities.City;
import com.arthuurdp.e_commerce.domain.entities.State;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "city", source = "city")
    @Mapping(target = "state", source = "city.state")
    AddressResponse toAddressResponse(Address address);

    CityResponse toCityResponse(City city);

    StateResponse toStateResponse(State state);
}
