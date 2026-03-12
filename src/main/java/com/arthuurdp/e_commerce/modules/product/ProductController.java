package com.arthuurdp.e_commerce.modules.product;

import com.arthuurdp.e_commerce.modules.product.dtos.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(service.findAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CreateProductResponse> create(
            @RequestBody @Valid CreateProductRequest req
    ) {
        CreateProductResponse response = service.register(req);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<UpdateProductResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProductRequest req
    ) {
        return ResponseEntity.ok().body(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/main-image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDetailsResponse> setMainImage(
            @PathVariable Long id,
            @RequestBody SetMainImageRequest req
    ) {
        return ResponseEntity.ok().body(service.setMainImage(id, req));
    }
}
