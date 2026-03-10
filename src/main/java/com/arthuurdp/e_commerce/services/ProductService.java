package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.dtos.product.*;
import com.arthuurdp.e_commerce.domain.entities.Product;
import com.arthuurdp.e_commerce.domain.entities.ProductImage;
import com.arthuurdp.e_commerce.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.ProductRepository;
import com.arthuurdp.e_commerce.services.mappers.ProductMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository repo;
    private final CategoryService categoryService;
    private final ProductMapper mapper;

    public ProductService(ProductRepository repo, CategoryService categoryService, ProductMapper mapper) {
        this.repo = repo;
        this.categoryService = categoryService;
        this.mapper = mapper;
    }

    @Transactional
    public Page<ProductResponse> findAll(int page, int size) {
        return repo.findAll(PageRequest.of(page, size)).map(mapper::toProductDTO);
    }

    @Transactional
    public ProductDetailsResponse findById(Long id) {
        return mapper.toProductDetails(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found")));
    }

    @Transactional
    public CreateProductResponse register(CreateProductRequest req) {
        Product product = new Product(
                req.name(),
                req.description(),
                req.price(),
                req.stock(),
                req.weight(),
                req.width(),
                req.height(),
                req.length()
        );
        product.addCategories(req.categoryIds().stream().map(categoryService::findEntityById).toList());
        product.addImages(req.images().stream().map(mapper::toProductImage).toList());

        if (req.mainImageUrl() != null) {
            product.getImages().stream()
                    .filter(img -> img.getUrl().equals(req.mainImageUrl()))
                    .findFirst()
                    .ifPresent(product::setMainImage);
        } else if (!product.getImages().isEmpty()) {
            product.setMainImage(product.getImages().get(0));
        }

        return mapper.toCreateResponse(repo.save(product));
    }

    @Transactional
    public UpdateProductResponse update(Long id, UpdateProductRequest req) {
        Product product = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (req.name() != null) {
            product.setName(req.name());
        }

        if (req.description() != null) {
            product.setDescription(req.description());
        }

        if (req.price() != null) {
            product.setPrice(req.price());
        }

        if (req.stock() != null) {
            product.setStock(req.stock());
        }
        
        if (req.weight() != null) {
            product.setWeight(req.weight());
        }

        if (req.width() != null) {
            product.setWidth(req.width());
        }

        if (req.height() != null) {
            product.setHeight(req.height());
        }

        if (req.length() != null) {
            product.setLength(req.length());
        }

        if (req.imageUrls() != null) {
            product.removeAllImages();
            product.addImages(req.imageUrls().stream().map(mapper::toProductImage).toList());

            if (req.mainImageUrl() != null) {
                product.getImages().stream()
                        .filter(img -> img.getUrl().equals(req.mainImageUrl()))
                        .findFirst()
                        .ifPresent(product::setMainImage);
            } else if (!product.getImages().isEmpty()) {
                product.setMainImage(product.getImages().get(0));
            }
        } else if (req.mainImageUrl() != null) {
            product.getImages().stream()
                    .filter(img -> img.getUrl().equals(req.mainImageUrl()))
                    .findFirst()
                    .ifPresent(product::setMainImage);
        }

        if (req.categoryIds() != null) {
            product.removeAllCategories();
            product.addCategories(req.categoryIds().stream().map(categoryService::findEntityById).toList());
        }

        return mapper.toUpdateResponse(repo.save(product));
    }

    public void delete(Long id) {
        repo.delete(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    @Transactional
    public void decreaseStock(Product product, int quantity) {
        if (product.getStock() < quantity) {
            throw new BadRequestException("Product " + product.getName() + " has insufficient stock.");
        }
        product.setStock(product.getStock() - quantity);
        repo.save(product);
    }

    @Transactional
    public void setMainImage(Long productId, SetMainImageRequest req) {
        Product product = repo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductImage image = product.getImages().stream()
                .filter(img -> img.getId().equals(req.id()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Image not found for this product"));

        product.setMainImage(image);
        repo.save(product);
    }
}
