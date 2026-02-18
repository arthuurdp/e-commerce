package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.Category;
import com.arthuurdp.e_commerce.entities.Product;
import com.arthuurdp.e_commerce.entities.ProductImage;
import com.arthuurdp.e_commerce.entities.dtos.category.CategoryDAO;
import com.arthuurdp.e_commerce.entities.dtos.product.ProductDAO;
import com.arthuurdp.e_commerce.entities.dtos.product.RegisterProductResponse;
import com.arthuurdp.e_commerce.entities.dtos.product.UpdateProductResponse;
import org.springframework.stereotype.Component;

@Component
public class EntityMapperService {
    public CategoryDAO toCategoryResponse(Category category) {
        return new CategoryDAO(
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

    public ProductDAO toProductDAO(Product product) {
        return new ProductDAO(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImages()
        );
    }

    public ProductImage toProductImage(String url) {
        return new ProductImage(
                url
        );
    }
}
