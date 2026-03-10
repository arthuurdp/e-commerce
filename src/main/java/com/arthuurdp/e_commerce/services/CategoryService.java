package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.dtos.category.UpdateCategoryRequest;
import com.arthuurdp.e_commerce.domain.entities.Category;
import com.arthuurdp.e_commerce.domain.dtos.category.CreateCategoryRequest;
import com.arthuurdp.e_commerce.domain.dtos.category.CategoryResponse;
import com.arthuurdp.e_commerce.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CategoryRepository;
import com.arthuurdp.e_commerce.services.mappers.ProductMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository repo;
    private final ProductMapper mapper;

    public CategoryService(CategoryRepository repo, ProductMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public Category findEntityById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public Page<CategoryResponse> findAll(int page, int size) {
        return repo.findAll(PageRequest.of(page, size)).map(mapper::toCategoryResponse);
    }

    public CategoryResponse findById(Long id) {
        return mapper.toCategoryResponse(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found")));
    }

    public CategoryResponse create(CreateCategoryRequest req) {
        return mapper.toCategoryResponse(repo.save(new Category(req.name())));
    }

    @Transactional
    public CategoryResponse update(Long id, UpdateCategoryRequest req) {
        Category category = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (req.name() != null) {
            category.setName(req.name());
        }

        return mapper.toCategoryResponse(repo.save(category));
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
