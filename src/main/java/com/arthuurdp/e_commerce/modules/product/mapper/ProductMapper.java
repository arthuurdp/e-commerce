package com.arthuurdp.e_commerce.modules.product.mapper;

import com.arthuurdp.e_commerce.modules.product.dtos.CreateProductResponse;
import com.arthuurdp.e_commerce.modules.product.dtos.ProductDetailsResponse;
import com.arthuurdp.e_commerce.modules.product.dtos.ProductResponse;
import com.arthuurdp.e_commerce.modules.product.dtos.UpdateProductResponse;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.modules.product.entity.ProductImage;
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

    default ProductImage toProductImage(String url) {
        return new ProductImage(url);
    }
}
