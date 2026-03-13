package com.arthuurdp.e_commerce.modules.product;

import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.modules.product.entity.ProductImage;
import com.arthuurdp.e_commerce.modules.product.entity.ProductSpecification;
import com.arthuurdp.e_commerce.shared.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.modules.product.dtos.*;
import com.arthuurdp.e_commerce.modules.category.CategoryService;
import com.arthuurdp.e_commerce.modules.product.mapper.ProductMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Page<ProductResponse> findAll(int page, int size, String name, List<Long> categoryIds) {
        Specification<Product> spec = Specification.allOf(
                ProductSpecification.nameContains(name),
                ProductSpecification.inCategories(categoryIds)
        );

        return repo.findAll(spec, PageRequest.of(page, size)).map(mapper::toProductResponse);
    }

    @Transactional
    public ProductDetailsResponse findById(Long id) {
        return mapper.toProductDetailsResponse(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found")));
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

        if (req.mainImageRequest() != null) {
            applyMainImage(product, req.mainImageRequest());
        } else if (!product.getImages().isEmpty()) {
            product.setMainImage(product.getImages().get(0));
        }

        return mapper.toCreateResponse(repo.save(product));
    }

    @Transactional
    public UpdateProductResponse update(Long id, UpdateProductRequest req) {
        Product product = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Optional.ofNullable(req.name()).ifPresent(product::setName);
        Optional.ofNullable(req.description()).ifPresent(product::setDescription);
        Optional.ofNullable(req.price()).ifPresent(product::setPrice);
        Optional.ofNullable(req.stock()).ifPresent(product::setStock);
        Optional.ofNullable(req.weight()).ifPresent(product::setWeight);
        Optional.ofNullable(req.width()).ifPresent(product::setWidth);
        Optional.ofNullable(req.height()).ifPresent(product::setHeight);
        Optional.ofNullable(req.length()).ifPresent(product::setLength);

        if (req.imageUrls() != null) {
            product.removeAllImages();
            product.addImages(req.imageUrls().stream().map(mapper::toProductImage).toList());
            repo.saveAndFlush(product);

            if (req.mainImageRequest() != null) {
                applyMainImage(product, req.mainImageRequest());
            } else if (!product.getImages().isEmpty()) {
                product.setMainImage(product.getImages().get(0));
            }
        } else if (req.mainImageRequest() != null) {
            applyMainImage(product, req.mainImageRequest());
        }

        if (req.categoryIds() != null) {
            product.removeAllCategories();
            product.addCategories(req.categoryIds().stream().map(categoryService::findEntityById).toList());
        }

        return mapper.toUpdateResponse(repo.save(product));
    }

    public void delete(Long id) {
        repo.delete(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found")));
    }

    @Transactional
    public void decreaseStock(Product product, int quantity) {
        if (product.getStock() < quantity) {
            throw new BadRequestException("Product " + product.getName() + " has insufficient stock.");
        }
        product.setStock(product.getStock() - quantity);
        repo.save(product);
    }

    public void restoreStock(Product product, int quantity) {
        product.setStock(product.getStock() + quantity);
        repo.save(product);
    }

    @Transactional
    public ProductDetailsResponse setMainImage(Long id, SetMainImageRequest req) {
        Product product = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductImage image = product.getImages().stream()
                .filter(img -> img.getId().equals(req.id()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Image not found for this product"));

        product.setMainImage(image);
        return mapper.toProductDetailsResponse(repo.save(product));
    }

    private void applyMainImage(Product product, SetMainImageRequest mainImageRequest) {
        product.getImages().stream()
                .filter(img -> img.getUrl().equals(mainImageRequest.id()))
                .findFirst()
                .ifPresent(product::setMainImage);
    }
}
