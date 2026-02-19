package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.*;
import com.arthuurdp.e_commerce.entities.dtos.cart_item.CartItemResponse;
import com.arthuurdp.e_commerce.entities.dtos.auth.RegisterResponse;
import com.arthuurdp.e_commerce.entities.dtos.category.CategoryResponse;
import com.arthuurdp.e_commerce.entities.dtos.product.ProductDTO;
import com.arthuurdp.e_commerce.entities.dtos.product.CreateProductResponse;
import com.arthuurdp.e_commerce.entities.dtos.product.UpdateProductResponse;
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
                product.getImages(),
                product.getCategories().stream().map(this::toCategoryResponse).toList(),
                product.getLastUpdatedAt()
        );
    }

    public ProductDTO toProductDTO(Product product) {
        return new ProductDTO(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImages()
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

    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        return  new CartItemResponse(
                cartItem.getId(),
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
                user.getEmail());
    }
}
