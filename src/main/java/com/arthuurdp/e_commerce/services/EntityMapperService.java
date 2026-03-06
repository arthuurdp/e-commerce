package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.*;
import com.arthuurdp.e_commerce.entities.dtos.carrier.CarrierResponse;
import com.arthuurdp.e_commerce.entities.dtos.order.OrderResponse;
import com.arthuurdp.e_commerce.entities.dtos.order_item.OrderItemResponse;
import com.arthuurdp.e_commerce.entities.dtos.order.OrderDetailsResponse;
import com.arthuurdp.e_commerce.entities.dtos.address.AddressResponse;
import com.arthuurdp.e_commerce.entities.dtos.places.CityResponse;
import com.arthuurdp.e_commerce.entities.dtos.places.StateResponse;
import com.arthuurdp.e_commerce.entities.dtos.cart.CartItemResponse;
import com.arthuurdp.e_commerce.entities.dtos.auth.RegisterResponse;
import com.arthuurdp.e_commerce.entities.dtos.cart.CartResponse;
import com.arthuurdp.e_commerce.entities.dtos.category.CategoryResponse;
import com.arthuurdp.e_commerce.entities.dtos.product.*;
import com.arthuurdp.e_commerce.entities.dtos.shipping.ShippingCarrierResponse;
import com.arthuurdp.e_commerce.entities.dtos.shipping.ShippingResponse;
import com.arthuurdp.e_commerce.entities.dtos.user.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class EntityMapperService {
    public CreateProductResponse toRegisterProductResponse(Product product) {
        return new CreateProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImages().stream().map(this::toProductImageResponse).toList(),
                product.getCategories().stream().map(this::toCategoryResponse).toList(),
                product.getCreatedAt()
        );
    }

    public UpdateProductResponse toUpdateProductResponse(Product product) {
        return new UpdateProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImages().stream().map(this::toProductImageResponse).toList(),
                product.getCategories().stream().map(this::toCategoryResponse).toList(),
                product.getLastUpdatedAt()
        );
    }

    public ProductResponse toProductDTO(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getMainImageUrl()
                );
    }

    public ProductDetailsResponse toProductDetails(Product product) {
        return new ProductDetailsResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImages().stream().map(this::toProductImageResponse).toList()
        );
    }

    public ProductImageResponse toProductImageResponse(ProductImage productImage) {
        return new ProductImageResponse(
                productImage.getId(),
                productImage.getUrl(),
                productImage.isMainImage()
        );
    }

    public CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }

    public ProductImage toProductImage(String url) {
        return new ProductImage(
                url
        );
    }

    public RegisterResponse toRegisterResponse(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    public CartResponse toCartResponse(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.quantity(),
                cart.getItems().stream().map(this::toCartItemResponse).toList(),
                cart.total()
        );
    }

    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        return  new CartItemResponse(
                cartItem.getProduct().getId(),
                cartItem.getProduct().getMainImageUrl(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getPrice(),
                cartItem.getQuantity(),
                cartItem.getSubtotal()
        );
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCpf(),
                user.getPhone(),
                user.getBirthDate(),
                user.getGender()
        );
    }

    public StateResponse toStateResponse(State state) {
        return new StateResponse(
                state.getId(),
                state.getName(),
                state.getUf()
        );
    }

    public CityResponse toCityResponse(City city) {
        return new CityResponse(
                city.getId(),
                city.getName()
        );
    }

    public AddressResponse toAddressResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getName(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                toCityResponse(address.getCity()),
                toStateResponse(address.getCity().getState())
        );
    }

    public OrderDetailsResponse toOrderDetailsResponse(Order order) {
        return new OrderDetailsResponse(
                order.getId(),
                order.getStatus(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getItems().stream().map(this::toOrderItemResponse).toList()
        );
    }

    public OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotal(),
                order.getTotalItems(),
                order.getCreatedAt()
        );
    }

    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getSubtotal()
        );
    }

    public ShippingCarrierResponse toShippingCarrierResponse(ShippingCarrier shippingCarrier) {
        return new ShippingCarrierResponse(
                shippingCarrier.getId(),
                shippingCarrier.getShipping().getId(),
                toCarrierResponse(shippingCarrier.getCarrier()),
                toStateResponse(shippingCarrier.getState()),
                shippingCarrier.getLegOrder(),
                shippingCarrier.getStatus()
        );
    }

    public ShippingResponse toShippingResponse(Shipping shipping) {
        return new ShippingResponse(
                shipping.getId(),
                shipping.getOrder().getId(),
                shipping.getStatus(),
                shipping.getCarriers().stream().map(this::toShippingCarrierResponse).toList(),
                shipping.getTrackingCode(),
                shipping.getShippedAt(),
                shipping.getDeliveredAt(),
                shipping.getCreatedAt()
        );
    }

    public CarrierResponse toCarrierResponse(Carrier carrier) {
        return new CarrierResponse(
                carrier.getId(),
                carrier.getName(),
                carrier.getStatus(),
                toStateResponse(carrier.getState())
        );
    }
}
