package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.Product;
import com.arthuurdp.e_commerce.entities.dtos.product.*;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final EntityMapperService entityMapperService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService, EntityMapperService entityMapperService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.entityMapperService = entityMapperService;
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public Page<Product> findAll(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size));
    }

    public Page<ProductDTO> findAllResponse(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size)).map(entityMapperService::toProductDTO);
    }

    public ProductDTO findByIdResponse(Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return entityMapperService.toProductDTO(p);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CreateProductResponse register(CreateProductRequest product) {
        Product p = new Product(
                product.name(),
                product.description(),
                product.price(),
                product.stock()
        );
        p.addCategories(product.categoryIds().stream().map(categoryService::findById).toList());
        p.addImages(product.images().stream().map(entityMapperService::toProductImage).toList());

        productRepository.save(p);
        return entityMapperService.toRegisterProductResponse(p);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UpdateProductResponse update(Long id, UpdateProductRequest product) {
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.name() != null) {
            p.setName(product.name());
        }

        if (product.description() != null) {
            p.setDescription(product.description());
        }

        if (product.price() != null) {
            p.setPrice(product.price());
        }

        if (product.stock() != null) {
            p.setStock(product.stock());
        }

        if (product.images() != null) {
            p.removeAllImages();
            p.addImages(product.images().stream().map(entityMapperService::toProductImage).toList());
        }

        if (product.categoryIds() != null) {
            p.removeAllCategories();
            p.addCategories(product.categoryIds().stream().map(categoryService::findById).toList());
        }

        return entityMapperService.toUpdateProductResponse(productRepository.save(p));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        productRepository.delete(product);
    }
}

