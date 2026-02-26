package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.Order;
import com.arthuurdp.e_commerce.entities.Payment;
import com.arthuurdp.e_commerce.entities.enums.OrderStatus;
import com.arthuurdp.e_commerce.entities.enums.PaymentStatus;
import com.arthuurdp.e_commerce.repositories.OrderRepository;
import com.arthuurdp.e_commerce.repositories.PaymentRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WebhookService {
    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final EntityMapperService entityMapperService;

    public WebhookService(PaymentRepository paymentRepository, OrderRepository orderRepository, EntityMapperService entityMapperService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.entityMapperService = entityMapperService;
    }

    @Transactional
    public void handlePaymentNotification(String mpPaymentId) {
        com.mercadopago.resources.payment.Payment mpPayment;
        try {
            PaymentClient client = new PaymentClient();
            mpPayment = client.get(Long.parseLong(mpPaymentId));
        } catch (MPException | MPApiException e) {
            log.error("Failed to fetch payment {} from Mercado Pago: {}", mpPaymentId, e.getMessage());
            return;
        }

        Payment payment = paymentRepository.findByTransactionId(mpPaymentId).orElse(null);
        if (payment == null) {
            log.warn("Received webhook for unknown transactionId: {}", mpPaymentId);
            return;
        }

        PaymentStatus newStatus = entityMapperService.fromMercadoPagoStatus(mpPayment.getStatus());

        if (payment.getStatus() == newStatus) {
            log.info("Payment {} already has status {}, skipping update.", mpPaymentId, newStatus);
            return;
        }

        payment.setStatus(newStatus);

        if (newStatus == PaymentStatus.APPROVED) {
            payment.setPaidAt(LocalDateTime.now());
        }

        paymentRepository.save(payment);

        Order order = payment.getOrder();
        if (order == null) {
            log.warn("Payment {} has no associated order.", mpPaymentId);
            return;
        }

        OrderStatus newOrderStatus = switch (newStatus) {
            case APPROVED -> OrderStatus.PAID;
            case REJECTED, REFUNDED -> OrderStatus.CANCELED;
            default -> order.getStatus();
        };

        if (order.getStatus() != newOrderStatus) {
            order.setStatus(newOrderStatus);
            orderRepository.save(order);
            log.info("Order {} status updated to {}", order.getId(), newOrderStatus);
        }
    }
}
