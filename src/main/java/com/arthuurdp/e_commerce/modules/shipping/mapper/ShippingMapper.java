package com.arthuurdp.e_commerce.modules.shipping.mapper;

import com.arthuurdp.e_commerce.modules.shipping.dtos.ShippingResponse;
import com.arthuurdp.e_commerce.modules.shipping.entity.Shipping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShippingMapper {
    @Mapping(target = "orderId", source = "order.id")
    ShippingResponse toShippingResponse(Shipping shipping);
}
