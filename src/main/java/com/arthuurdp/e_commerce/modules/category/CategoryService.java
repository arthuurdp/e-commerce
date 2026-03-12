package com.arthuurdp.e_commerce.modules.category;

import com.arthuurdp.e_commerce.modules.category.mapper.CategoryMapper;
import com.arthuurdp.e_commerce.modules.category.dtos.UpdateCategoryRequest;
import com.arthuurdp.e_commerce.modules.category.entity.Category;
import com.arthuurdp.e_commerce.modules.category.dtos.CreateCategoryRequest;
import com.arthuurdp.e_commerce.modules.category.dtos.CategoryResponse;
import com.arthuurdp.e_commerce.shared.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository repo;
    private final CategoryMapper mapper;

    public CategoryService(CategoryRepository repo, CategoryMapper mapper) {
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

        Optional.ofNullable(req.name()).ifPresent(category::setName);

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
