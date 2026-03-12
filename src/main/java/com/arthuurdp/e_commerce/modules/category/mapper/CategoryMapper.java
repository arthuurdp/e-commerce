package com.arthuurdp.e_commerce.modules.category.mapper;

import com.arthuurdp.e_commerce.modules.category.entity.Category;
import com.arthuurdp.e_commerce.modules.category.dtos.CategoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toCategoryResponse(Category category);
}
