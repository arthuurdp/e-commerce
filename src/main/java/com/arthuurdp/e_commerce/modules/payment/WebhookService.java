package com.arthuurdp.e_commerce.modules.payment;

import com.arthuurdp.e_commerce.shared.exceptions.WebhookException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {
    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final WebhookHandlerService handlerService;

    public WebhookService(WebhookHandlerService handlerService) {
        this.handlerService = handlerService;
    }

    public void handleEvent(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature: {}", e.getMessage());
            throw new WebhookException("Invalid webhook signature");
        }

        log.info("Stripe event received: {}", event.getType());

        switch (event.getType()) {
            case "checkout.session.completed" -> {
                try {
                    handlerService.handleSessionCompleted(event);
                } catch (Exception e) {
                    log.error("Error handling checkout.session.completed: {}", e.getMessage(), e);
                }
            }
            case "checkout.session.expired" -> {
                try {
                    handlerService.handleSessionExpired(event);
                } catch (Exception e) {
                    log.error("Error handling checkout.session.expired: {}", e.getMessage(), e);
                }
            }
            default -> log.info("Unhandled event type: {}", event.getType());
        }
    }
}