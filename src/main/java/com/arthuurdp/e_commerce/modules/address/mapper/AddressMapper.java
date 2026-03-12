package com.arthuurdp.e_commerce.modules.address.mapper;

import com.arthuurdp.e_commerce.modules.address.dtos.AddressResponse;
import com.arthuurdp.e_commerce.modules.address.dtos.CityResponse;
import com.arthuurdp.e_commerce.modules.address.dtos.StateResponse;
import com.arthuurdp.e_commerce.modules.address.entity.Address;
import com.arthuurdp.e_commerce.modules.address.entity.City;
import com.arthuurdp.e_commerce.modules.address.entity.State;
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
