package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.checkout.CheckoutRequest;
import com.arthuurdp.e_commerce.entities.dtos.checkout.CheckoutResponse;
import com.arthuurdp.e_commerce.services.CheckoutService;
import com.arthuurdp.e_commerce.services.WebhookService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final WebhookService webhookService;

    public CheckoutController(CheckoutService checkoutService, WebhookService webhookService) {
        this.checkoutService = checkoutService;
        this.webhookService = webhookService;
    }

    @PostMapping("/orders/checkout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody @Valid CheckoutRequest req)
            throws StripeException {
        return ResponseEntity.ok(checkoutService.checkout(req));
    }

    @PostMapping("/webhook/stripe")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        webhookService.handleEvent(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/checkout/success")
    public ResponseEntity<String> success(@RequestParam String session_id) {
        return ResponseEntity.ok("Pagamento realizado com sucesso! Session: " + session_id);
    }

    @GetMapping("/checkout/failure")
    public ResponseEntity<String> failure() {
        return ResponseEntity.ok("Pagamento cancelado. Tente novamente.");
    }
}