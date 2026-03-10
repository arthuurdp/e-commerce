package com.arthuurdp.e_commerce.services.mappers;

import com.arthuurdp.e_commerce.domain.dtos.carrier.CarrierResponse;
import com.arthuurdp.e_commerce.domain.entities.Carrier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarrierMapper {
    CarrierResponse toCarrierResponse(Carrier carrier);
}
