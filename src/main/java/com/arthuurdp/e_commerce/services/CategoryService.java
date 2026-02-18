package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.Category;
import com.arthuurdp.e_commerce.entities.dtos.category.CategoryDAO;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EntityMapperService entityMapperService;

    public CategoryService(CategoryRepository categoryRepository, EntityMapperService entityMapperService) {
        this.categoryRepository = categoryRepository;
        this.entityMapperService = entityMapperService;
    }

    public Page<Category> findAll(int page, int size) {
        return categoryRepository.findAll(PageRequest.of(page, size));
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public Page<CategoryDAO> findAllResponse(int page, int size) {
        return categoryRepository.findAll(PageRequest.of(page, size)).map(entityMapperService::toCategoryResponse);
    }

    public CategoryDAO findByIdResponse(Long id) {
        return entityMapperService.toCategoryResponse(findById(id));
    }

    public CategoryDAO create(Category category) {
        return entityMapperService.toCategoryResponse(categoryRepository.save(category));
    }

    public CategoryDAO update(Long id, Category category) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (category.getName() != null && !category.getName().isBlank()) {
            existingCategory.setName(category.getName());
        }

        return entityMapperService.toCategoryResponse(categoryRepository.save(existingCategory));
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        categoryRepository.deleteById(id);
    }
}
