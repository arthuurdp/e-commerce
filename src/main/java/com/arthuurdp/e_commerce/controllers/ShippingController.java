package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.domain.dtos.shipping.ShippingResponse;
import com.arthuurdp.e_commerce.domain.dtos.shipping.UpdateShippingRequest;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.services.ShippingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("orders/{orderId}/shipping")
public class ShippingController {
    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ShippingResponse> findByOrderId(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok().body(shippingService.findByOrderId(orderId, user));
    }

    @PatchMapping("/{shippingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShippingResponse> update(
            @PathVariable Long shippingId,
            @RequestBody @Valid UpdateShippingRequest req) {
        return ResponseEntity.ok(shippingService.update(shippingId, req));
    }
}
