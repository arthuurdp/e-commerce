package com.arthuurdp.e_commerce.entities.dtos.product;

import com.arthuurdp.e_commerce.entities.ProductImage;

import java.util.List;

public record ProductDTO(String name, String description, Double price, List<ProductImage> imgs) {
}
