package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.*;
import com.arthuurdp.e_commerce.entities.cart_item.CartItemResponse;
import com.arthuurdp.e_commerce.entities.dtos.auth.RegisterResponse;
import com.arthuurdp.e_commerce.entities.dtos.category.CategoryResponse;
import com.arthuurdp.e_commerce.entities.dtos.category.CreateCategoryResponse;
import com.arthuurdp.e_commerce.entities.dtos.product.ProductDTO;
import com.arthuurdp.e_commerce.entities.dtos.product.CreateProductResponse;
import com.arthuurdp.e_commerce.entities.dtos.product.UpdateProductResponse;
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

    public UpdateProductResponse toProductUpdateResponse(Product product) {
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

    public CreateCategoryResponse toCreateCategoryResponse(Category category) {
        return new CreateCategoryResponse(
                category.getId(),
                category.getName()
        );
    }

    public CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
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
                user.getEmail(),
                user.getPassword()
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
}
