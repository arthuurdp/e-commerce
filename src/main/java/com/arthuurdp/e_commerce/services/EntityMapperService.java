package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.Category;
import com.arthuurdp.e_commerce.entities.Product;
import com.arthuurdp.e_commerce.entities.dtos.CategoryResponse;
import com.arthuurdp.e_commerce.entities.dtos.RegisterProductResponse;
import com.arthuurdp.e_commerce.entities.dtos.UpdateProductResponse;
import org.springframework.stereotype.Component;

@Component
public class EntityMapperService {
    public CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }

    public RegisterProductResponse toRegisterProductResponse(Product product) {
        return new RegisterProductResponse(
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
                product.getCategories().stream().map(this::toCategoryResponse).toList(),
                product.getLastUpdatedAt()
        );
    }
}
