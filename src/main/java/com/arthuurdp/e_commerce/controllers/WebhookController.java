package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.checkout.MercadoPagoWebhookRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/mercadopago")
    public ResponseEntity<Void> handleMercadoPago(@RequestBody MercadoPagoWebhookRequest req) {
        if ("payment".equals(req.type()) && req.data() != null) {
            webhookService.handlePaymentNotification(req.data().id());
        }
        return ResponseEntity.ok().build();
    }
}