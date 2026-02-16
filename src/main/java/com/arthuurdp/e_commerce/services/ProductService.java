package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.Category;
import com.arthuurdp.e_commerce.entities.Product;
import com.arthuurdp.e_commerce.entities.dtos.RegisterProductRequest;
import com.arthuurdp.e_commerce.entities.dtos.RegisterProductResponse;
import com.arthuurdp.e_commerce.entities.dtos.UpdateProductRequest;
import com.arthuurdp.e_commerce.entities.dtos.UpdateProductResponse;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repo;
    private final EntityMapperService entityMapper;

    public ProductService(ProductRepository repo, EntityMapperService entityMapper) {
        this.repo = repo;
        this.entityMapper = entityMapper;
    }

    public Product findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public Page<Product> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = repo.findAll(pageable);
        return productPage;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RegisterProductResponse registerProduct(RegisterProductRequest product) {
        Product p = new Product(
                product.name(),
                product.description(),
                product.price(),
                product.stock(),
                product.images()
        );

        repo.save(p);
        return entityMapper.toRegisterProductResponse(p);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UpdateProductResponse updateProduct(Long id, UpdateProductRequest product) {
        Product p = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.name() != null && !product.name().isBlank()) {
            p.setName(product.name());
        }

        if (product.description() != null && !product.description().isBlank()) {
            p.setDescription(product.description());
        }

        if (product.price() != null && !product.price().isNaN()) {
            p.setPrice(product.price());
        }

        if (product.stock() != null && product.stock() >= 0) {
            p.setStock(product.stock());
        }

        if (product.images() != null && !product.images().isEmpty()) {
            product.images().forEach(p::addImage);
        }

        if (product.categories() != null) {
            product.categories().forEach(p::addCategory);
        }

        p.setLastUpdatedAt(Instant.now());
        repo.save(p);

        return entityMapper.toProductUpdateResponse(p);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Long id) {
        Product product = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        repo.delete(product);
    }
}

