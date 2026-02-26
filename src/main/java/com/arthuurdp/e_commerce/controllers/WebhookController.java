package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.checkout.mp.MercadoPagoWebhookRequest;
import com.arthuurdp.e_commerce.services.WebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Receives payment notifications from Mercado Pago.
     *
     * Mercado Pago sends a POST with:
     * {
     *   "action": "payment.updated",
     *   "type": "payment",
     *   "data": { "id": "123456789" }
     * }
     *
     * Configure NOTIFICATION_URL=https://your-domain.com/webhooks/mercadopago
     * in your .env when you have a public URL.
     */
    @PostMapping("/mercadopago")
    public ResponseEntity<Void> handleMercadoPago(@RequestBody MercadoPagoWebhookRequest req) {
        if ("payment".equals(req.type()) && req.data() != null) {
            webhookService.handlePaymentNotification(req.data().id());
        }
        return ResponseEntity.ok().build();
    }
}