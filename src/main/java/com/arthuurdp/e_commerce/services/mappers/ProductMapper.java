package com.arthuurdp.e_commerce.services.mappers;

import com.arthuurdp.e_commerce.domain.dtos.product.*;
import com.arthuurdp.e_commerce.domain.dtos.category.CategoryResponse;
import com.arthuurdp.e_commerce.domain.entities.Category;
import com.arthuurdp.e_commerce.domain.entities.Product;
import com.arthuurdp.e_commerce.domain.entities.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "imgs", source = "images")
    CreateProductResponse toCreateResponse(Product product);

    @Mapping(target = "imgs", source = "images")
    @Mapping(target = "category", source = "categories")
    @Mapping(target = "updatedAt", source = "lastUpdatedAt")
    UpdateProductResponse toUpdateResponse(Product product);

    @Mapping(target = "mainImage", expression = "java(product.getMainImageUrl())")
    ProductResponse toProductDTO(Product product);

    @Mapping(target = "imgs", source = "images")
    ProductDetailsResponse toProductDetails(Product product);

    CategoryResponse toCategoryResponse(Category category);

    default ProductImage toProductImage(String url) {
        return new ProductImage(url);
    }
}
