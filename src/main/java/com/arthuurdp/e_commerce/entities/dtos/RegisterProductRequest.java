package com.arthuurdp.e_commerce.entities.dtos;

import java.util.List;

public record RegisterProductRequest(String name, String description, Double price, Integer stock, List<String> images, List<Long> categoriesId) {
}
