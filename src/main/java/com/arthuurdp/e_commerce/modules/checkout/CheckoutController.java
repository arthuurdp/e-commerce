package com.arthuurdp.e_commerce.modules.checkout;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import com.arthuurdp.e_commerce.modules.checkout.dtos.CheckoutRequest;
import com.arthuurdp.e_commerce.modules.checkout.dtos.CheckoutResponse;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/checkout/success")
    public ResponseEntity<String> success(
            @RequestParam String session_id
    ) {
        return ResponseEntity.ok("Pagamento realizado com sucesso! Session: " + session_id);
    }

    @GetMapping("/checkout/failure")
    public ResponseEntity<String> failure() {
        return ResponseEntity.ok("Pagamento cancelado. Tente novamente.");
    }
}