package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.domain.dtos.shipping.ShippingResponse;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.services.ShippingService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shippings")
public class ShippingController {
    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ShippingResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(shippingService.findAll(page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShippingResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(shippingService.findById(id));
    }

    @PatchMapping("/carriers/{id}/handoff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShippingResponse> handOff(@PathVariable Long id) {
        return ResponseEntity.ok().body(shippingService.handOff(id));
    }
}
