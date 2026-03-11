package com.arthuurdp.e_commerce.services.mappers;

import com.arthuurdp.e_commerce.domain.dtos.cart.CartItemResponse;
import com.arthuurdp.e_commerce.domain.dtos.cart.CartResponse;
import com.arthuurdp.e_commerce.domain.entities.Cart;
import com.arthuurdp.e_commerce.domain.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalQuantity", expression = "java(cart.quantity())")
    @Mapping(target = "total", expression = "java(cart.total())")
    CartResponse toCartResponse(Cart cart);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "imageUrl", source = "product.mainImageUrl")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "subtotal", expression = "java(cartItem.getSubtotal())")
    CartItemResponse toCartItemResponse(CartItem cartItem);
}
