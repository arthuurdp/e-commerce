package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.shipping.ShippingResponse;
import com.arthuurdp.e_commerce.entities.dtos.shipping.UpdateShippingRequest;
import com.arthuurdp.e_commerce.services.ShippingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("orders/{orderId}/shipping")
public class ShippingController {
    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping
    public ResponseEntity<ShippingResponse> findByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok().body(shippingService.findByOrderId(orderId));
    }

    @PatchMapping("/{shippingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShippingResponse> update(
            @PathVariable Long orderId,
            @PathVariable Long shippingId,
            @RequestBody @Valid UpdateShippingRequest req
    ) {
        return ResponseEntity.ok(shippingService.update(shippingId, req));
    }
}
