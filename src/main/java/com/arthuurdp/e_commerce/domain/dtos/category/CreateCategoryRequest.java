package com.arthuurdp.e_commerce.domain.dtos.category;

import jakarta.validation.constraints.NotNull;

public record CreateCategoryRequest(
        @NotNull(message = "Name is required")
        String name
) {}
