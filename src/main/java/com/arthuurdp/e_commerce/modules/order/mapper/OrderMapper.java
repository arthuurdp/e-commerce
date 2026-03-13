package com.arthuurdp.e_commerce.modules.order.mapper;

import com.arthuurdp.e_commerce.modules.order.dtos.OrderDetailsResponse;
import com.arthuurdp.e_commerce.modules.order.dtos.OrderResponse;
import com.arthuurdp.e_commerce.modules.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "items", source = "items")
    OrderDetailsResponse toOrderDetailsResponse(Order order);

    @Mapping(target = "totalItems", expression = "java(order.getItems().size())")
    OrderResponse toOrderResponse(Order order);
}
