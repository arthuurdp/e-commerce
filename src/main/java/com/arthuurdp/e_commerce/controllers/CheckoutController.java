package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.checkout.CheckoutRequest;
import com.arthuurdp.e_commerce.entities.dtos.checkout.CheckoutResponse;
import com.arthuurdp.e_commerce.services.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@PreAuthorize("hasRole('USER')")
public class CheckoutController {
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody @Valid CheckoutRequest req) {
        return ResponseEntity.ok().body(checkoutService.checkout(req));
    }
}
