package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.Category;
import com.arthuurdp.e_commerce.entities.dtos.category.CategoryRequest;
import com.arthuurdp.e_commerce.entities.dtos.category.CategoryResponse;
import com.arthuurdp.e_commerce.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository repo;
    private final EntityMapperService entityMapperService;

    public CategoryService(CategoryRepository repo, EntityMapperService entityMapperService) {
        this.repo = repo;
        this.entityMapperService = entityMapperService;
    }

    public Category findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public Page<CategoryResponse> findAllResponse(int page, int size) {
        return repo.findAll(PageRequest.of(page, size)).map(entityMapperService::toCategoryResponse);
    }

    public CategoryResponse findByIdResponse(Long id) {
        return entityMapperService.toCategoryResponse(findById(id));
    }

    public CategoryResponse create(CategoryRequest category) {
        return entityMapperService.toCategoryResponse(repo.save(new Category(category.name())));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest category) {
        Category existingCategory = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (category.name() != null && !category.name().isBlank()) {
            existingCategory.setName(category.name());
        }

        return entityMapperService.toCategoryResponse(repo.save(existingCategory));
    }

    @Transactional
    public void delete(Long id) {
        Category category = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getProducts().isEmpty()) {
            throw new BadRequestException("Cannot delete a category that has products associated");
        }

        repo.delete(category);
    }
}
