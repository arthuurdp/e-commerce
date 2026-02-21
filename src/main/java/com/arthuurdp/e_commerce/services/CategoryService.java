package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.Category;
import com.arthuurdp.e_commerce.entities.dtos.category.CategoryRequest;
import com.arthuurdp.e_commerce.entities.dtos.category.CategoryResponse;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EntityMapperService entityMapperService;

    public CategoryService(CategoryRepository categoryRepository, EntityMapperService entityMapperService) {
        this.categoryRepository = categoryRepository;
        this.entityMapperService = entityMapperService;
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public Page<CategoryResponse> findAllResponse(int page, int size) {
        return categoryRepository.findAll(PageRequest.of(page, size)).map(entityMapperService::toCategoryResponse);
    }

    public CategoryResponse findByIdResponse(Long id) {
        return entityMapperService.toCategoryResponse(findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse create(CategoryRequest category) {
        return entityMapperService.toCategoryResponse(categoryRepository.save(new Category(category.name())));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse update(Long id, CategoryRequest category) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (category.name() != null && !category.name().isBlank()) {
            existingCategory.setName(category.name());
        }

        return entityMapperService.toCategoryResponse(categoryRepository.save(existingCategory));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        categoryRepository.deleteById(id);
    }
}
