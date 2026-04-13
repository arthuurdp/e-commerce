package com.arthuurdp.e_commerce.modules.product.mapper;

import com.arthuurdp.e_commerce.modules.product.dtos.*;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.modules.product.entity.ProductImage;
import com.arthuurdp.e_commerce.modules.review.repository.ReviewRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {
    @Autowired
    protected ReviewRepository reviewRepository;

    @Mapping(target = "imgs", source = "images")
    public abstract CreateProductResponse toCreateResponse(Product product);

    @Mapping(target = "imgs", source = "images")
    @Mapping(target = "categories", source = "categories")
    @Mapping(target = "updatedAt", source = "lastUpdatedAt")
    public abstract UpdateProductResponse toUpdateResponse(Product product);

    @Mapping(target = "mainImage", expression = "java(product.getMainImageUrl())")
    @Mapping(target = "averageRating", expression = "java(reviewRepository.getAverageRatingByProductId(product.getId()) != null ? reviewRepository.getAverageRatingByProductId(product.getId()) : 0.0)")
    @Mapping(target = "reviewCount", expression = "java(reviewRepository.countByProductId(product.getId()))")
    public abstract ProductResponse toProductResponse(Product product);

    @Mapping(target = "categories", source = "categories")
    @Mapping(target = "imgs", source = "images")
    @Mapping(target = "averageRating", expression = "java(reviewRepository.getAverageRatingByProductId(product.getId()) != null ? reviewRepository.getAverageRatingByProductId(product.getId()) : 0.0)")
    @Mapping(target = "reviewCount", expression = "java(reviewRepository.countByProductId(product.getId()))")
    public abstract ProductDetailsResponse toProductDetailsResponse(Product product);

    public ProductImage toProductImage(String url) {
        return new ProductImage(url);
    }
}
