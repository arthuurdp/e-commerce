package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.domain.dtos.shipping.MelhorEnvioWebhookEvent;
import com.arthuurdp.e_commerce.services.ShippingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/**
 * Receives tracking event webhooks from Melhor Envio.
 *
 * ME sends a POST request to this endpoint whenever a shipment status changes.
 * The request includes an HMAC-SHA256 signature in the X-Melhor-Envio-Signature
 * header that we verify to ensure the payload is legitimate.
 *
 * Required application.yml property:
 *   melhorenvio.webhook-secret=<your webhook secret from ME dashboard>
 */
@RestController
@RequestMapping("/webhooks/melhor-envio")
public class MelhorEnvioWebhookController {

    private static final Logger log = LoggerFactory.getLogger(MelhorEnvioWebhookController.class);
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final ShippingService shippingService;
    private final String webhookSecret;

    public MelhorEnvioWebhookController(
            ShippingService shippingService,
            @Value("${melhorenvio.webhook-secret}") String webhookSecret) {
        this.shippingService = shippingService;
        this.webhookSecret   = webhookSecret;
    }

    @PostMapping
    public ResponseEntity<Void> handleEvent(
            @RequestHeader(value = "X-Melhor-Envio-Signature", required = false) String signature,
            @RequestBody String rawPayload,
            @org.springframework.web.bind.annotation.RequestBody(required = false)
            MelhorEnvioWebhookEvent event) {

        // Verify signature before processing
        if (!isValidSignature(rawPayload, signature)) {
            log.warn("ME webhook received with invalid signature — rejecting");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (event == null || event.orderId() == null) {
            log.warn("ME webhook received with missing orderId — ignoring");
            return ResponseEntity.ok().build();
        }

        log.info("ME webhook: order={} status={}", event.orderId(), event.status());

        try {
            shippingService.handleWebhookEvent(event);
        } catch (Exception e) {
            // Log but return 200 to prevent ME from retrying indefinitely
            log.error("Error processing ME webhook for order {}: {}", event.orderId(), e.getMessage(), e);
        }

        return ResponseEntity.ok().build();
    }

    private boolean isValidSignature(String payload, String receivedSignature) {
        if (receivedSignature == null || receivedSignature.isBlank()) return false;
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expected = HexFormat.of().formatHex(hash);
            return expected.equalsIgnoreCase(receivedSignature);
        } catch (Exception e) {
            log.error("HMAC verification error: {}", e.getMessage());
            return false;
        }
    }
}