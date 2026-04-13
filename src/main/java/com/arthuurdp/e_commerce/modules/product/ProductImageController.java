package com.arthuurdp.e_commerce.modules.product;

import com.arthuurdp.e_commerce.modules.product.dtos.ProductDetailsResponse;
import com.arthuurdp.e_commerce.shared.storage.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products/{id}/images")
public class ProductImageController {
    private final ProductService service;
    private final FileStorageService fileStorageService;

    public ProductImageController(ProductService service, FileStorageService fileStorageService) {
        this.service = service;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDetailsResponse> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files,
            HttpServletRequest request
    ) {

        String baseUrl = "http://192.168.200.114:8080";

        List<String> imageUrls = files.stream()
                .map(fileStorageService::storeFile)
                .map(fileName -> baseUrl + "/uploads/" + fileName)
                .toList();

        return ResponseEntity.ok().body(service.addImages(id, imageUrls));
    }

    @PatchMapping("/{mainImageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDetailsResponse> setMainImage(
            @PathVariable Long id,
            @PathVariable Long mainImageId
    ) {
        return ResponseEntity.ok().body(service.setMainImage(id, mainImageId));
    }
}
