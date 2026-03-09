package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.domain.dtos.shipping.ShippingResponse;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.services.ShippingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders/{orderId}/shipping")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    /**
     * GET /orders/{orderId}/shipping
     * Returns the shipping details for the authenticated user's order.
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ShippingResponse> getShipping(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(shippingService.getShippingForUser(orderId, user.getId()));
    }
}