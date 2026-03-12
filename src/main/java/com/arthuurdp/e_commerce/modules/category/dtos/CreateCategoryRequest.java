package com.arthuurdp.e_commerce.modules.category.dtos;

import jakarta.validation.constraints.NotNull;

public record CreateCategoryRequest(
        @NotNull(message = "Name is required")
        String name
) {}
