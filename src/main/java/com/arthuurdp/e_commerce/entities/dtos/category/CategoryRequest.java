package com.arthuurdp.e_commerce.entities.dtos.category;

import jakarta.validation.constraints.NotNull;

public record CategoryRequest(@NotNull String name) {
}
