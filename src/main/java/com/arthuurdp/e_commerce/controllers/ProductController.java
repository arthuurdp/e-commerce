package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.Product;
import com.arthuurdp.e_commerce.entities.dtos.RegisterProductRequest;
import com.arthuurdp.e_commerce.entities.dtos.RegisterProductResponse;
import com.arthuurdp.e_commerce.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<Page<Product>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(service.findAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<RegisterProductResponse> registerProduct(@RequestBody @Valid RegisterProductRequest req) {
        RegisterProductResponse response = service.registerProduct(req);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(req.name())
                .toUri();
        return ResponseEntity.created(uri).body()
    }
}
