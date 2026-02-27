package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.*;
import com.arthuurdp.e_commerce.entities.dtos.OrderItemResponse;
import com.arthuurdp.e_commerce.entities.dtos.OrderResponse;
import com.arthuurdp.e_commerce.entities.dtos.address.AddressResponse;
import com.arthuurdp.e_commerce.entities.dtos.address.CityResponse;
import com.arthuurdp.e_commerce.entities.dtos.address.StateResponse;
import com.arthuurdp.e_commerce.entities.dtos.cart.CartItemResponse;
import com.arthuurdp.e_commerce.entities.dtos.auth.RegisterResponse;
import com.arthuurdp.e_commerce.entities.dtos.cart.CartResponse;
import com.arthuurdp.e_commerce.entities.dtos.category.CategoryResponse;
import com.arthuurdp.e_commerce.entities.dtos.product.*;
import com.arthuurdp.e_commerce.entities.dtos.user.UserResponse;
import com.arthuurdp.e_commerce.entities.enums.PaymentStatus;
import org.springframework.security.core.userdetails.UserDetails;
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

    public ProductDTO toProductDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getMainImageUrl()
                );
    }

    public ProductDetails toProductDetails(Product product) {
        return new ProductDetails(
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
                new CityResponse(
                        address.getCity().getId(),
                        address.getCity().getName()
                ),
                new StateResponse(
                        address.getCity().getState().getId(),
                        address.getCity().getState().getName(),
                        address.getCity().getState().getUf()
                )
        );
    }

    public PaymentStatus fromMercadoPagoStatus(String mpStatus) {
        return switch (mpStatus) {
            case "approved" -> PaymentStatus.APPROVED;
            case "rejected" -> PaymentStatus.REJECTED;
            default -> PaymentStatus.PENDING;
        };
    }

    public OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getItems().stream().map(this::toOrderItemResponse).toList()
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
}
