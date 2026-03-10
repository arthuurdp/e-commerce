package com.arthuurdp.e_commerce.services.mappers;

import com.arthuurdp.e_commerce.domain.dtos.order.OrderDetailsResponse;
import com.arthuurdp.e_commerce.domain.dtos.order.OrderResponse;
import com.arthuurdp.e_commerce.domain.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", source = "items")
    OrderDetailsResponse toOrderDetailsResponse(Order order);

    OrderResponse toOrderResponse(Order order);
}
