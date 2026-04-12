package com.arthuurdp.e_commerce.modules.checkout;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import com.arthuurdp.e_commerce.modules.checkout.dtos.CheckoutRequest;
import com.arthuurdp.e_commerce.modules.checkout.dtos.CheckoutResponse;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class CheckoutController {
    private final CheckoutService service;

    public CheckoutController(CheckoutService service) {
        this.service = service;
    }

    @PostMapping("/orders/checkout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CheckoutResponse> checkout(
            @RequestBody @Valid CheckoutRequest req,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) throws StripeException {
        return ResponseEntity.ok(service.checkout(req, authenticatedUser.getUser()));
    }

    @GetMapping("checkout/success")
    public ResponseEntity<Void> success(@RequestParam Long orderId) {
        URI deepLink = URI.create("ecommerce://payment/success?orderId=" + orderId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(deepLink)
                .build();
    }

    @GetMapping("checkout/failure")
    public ResponseEntity<Void> failure(@RequestParam Long orderId) {
        URI deepLink = URI.create("ecommerce://payment/failure?orderId=" + orderId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(deepLink)
                .build();
    }
}