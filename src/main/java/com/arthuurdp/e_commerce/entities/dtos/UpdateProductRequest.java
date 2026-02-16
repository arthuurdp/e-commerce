package com.arthuurdp.e_commerce.entities.dtos;

import com.arthuurdp.e_commerce.entities.Category;

import java.util.List;

public record UpdateProductRequest(String name, String description, Double price, Integer stock, List<String> images, List<Category> categories) {
}
