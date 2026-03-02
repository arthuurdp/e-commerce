package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.Order;
import com.arthuurdp.e_commerce.entities.Payment;
import com.arthuurdp.e_commerce.entities.enums.OrderStatus;
import com.arthuurdp.e_commerce.entities.enums.PaymentStatus;
import com.arthuurdp.e_commerce.repositories.OrderRepository;
import com.arthuurdp.e_commerce.repositories.PaymentRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public WebhookService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void handleEvent(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature: {}", e.getMessage());
            throw new RuntimeException("Invalid webhook signature");
        }

        log.info("Stripe event received: {}", event.getType());

        switch (event.getType()) {
            case "checkout.session.completed" -> handleSessionCompleted(event);
            case "checkout.session.expired" -> handleSessionExpired(event);
            default -> log.info("Unhandled event type: {}", event.getType());
        }
    }

    private Session deserializeSession(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        // Tenta deserializar normalmente
        if (deserializer.getObject().isPresent()) {
            StripeObject stripeObject = deserializer.getObject().get();
            if (stripeObject instanceof Session session) {
                return session;
            }
        }

        // Fallback: resolve incompatibilidade entre versão da SDK e versão da API do Stripe
        try {
            StripeObject stripeObject = deserializer.deserializeUnsafe();
            if (stripeObject instanceof Session session) {
                return session;
            }
        } catch (Exception e) {
            log.error("Failed to deserialize Stripe session (unsafe): {}", e.getMessage());
        }

        log.error("Could not deserialize Stripe session for event {}", event.getId());
        return null;
    }

    private void handleSessionCompleted(Event event) {
        Session session = deserializeSession(event);
        if (session == null) return;

        String orderId = session.getMetadata() != null ? session.getMetadata().get("orderId") : null;
        if (orderId == null) {
            log.warn("Session {} has no orderId in metadata", session.getId());
            return;
        }

        Order order = orderRepository.findById(Long.parseLong(orderId)).orElse(null);
        if (order == null) {
            log.warn("Order {} not found", orderId);
            return;
        }

        Payment payment = order.getPayment();
        if (payment == null) {
            log.warn("Order {} has no payment entity", orderId);
            return;
        }

        String paymentStatus = session.getPaymentStatus();
        log.info("Session {} paymentStatus: {}", session.getId(), paymentStatus);

        if ("paid".equals(paymentStatus)) {
            payment.setStatus(PaymentStatus.APPROVED);
            payment.setPaidAt(LocalDateTime.now());
            payment.setTransactionId(session.getPaymentIntent());
            order.setStatus(OrderStatus.PAID);
            log.info("Order {} marked as PAID", orderId);
        } else {
            payment.setStatus(PaymentStatus.PENDING);
            log.info("Order {} payment pending (status: {})", orderId, paymentStatus);
        }

        paymentRepository.save(payment);
        orderRepository.save(order);
    }

    private void handleSessionExpired(Event event) {
        Session session = deserializeSession(event);
        if (session == null) return;

        String orderId = session.getMetadata() != null ? session.getMetadata().get("orderId") : null;
        if (orderId == null) return;

        Order order = orderRepository.findById(Long.parseLong(orderId)).orElse(null);
        if (order == null) return;

        Payment payment = order.getPayment();
        if (payment != null) {
            payment.setStatus(PaymentStatus.REJECTED);
            paymentRepository.save(payment);
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
        log.info("Order {} canceled due to expired session", orderId);
    }
}