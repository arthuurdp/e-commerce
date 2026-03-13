package com.arthuurdp.e_commerce.modules.payment;

import com.arthuurdp.e_commerce.modules.order.entity.Order;
import com.arthuurdp.e_commerce.modules.payment.entity.Payment;
import com.arthuurdp.e_commerce.modules.order.enums.OrderStatus;
import com.arthuurdp.e_commerce.modules.payment.enums.PaymentStatus;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.modules.product.ProductService;
import com.arthuurdp.e_commerce.modules.order.OrderRepository;
import com.arthuurdp.e_commerce.modules.email.EmailSenderService;
import com.arthuurdp.e_commerce.modules.shipping.ShippingService;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WebhookHandlerService {
    private static final Logger log = LoggerFactory.getLogger(WebhookHandlerService.class);

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingService shippingService;
    private final EmailSenderService emailSenderService;
    private final ProductService productService;

    public WebhookHandlerService(OrderRepository orderRepository, PaymentRepository paymentRepository, ShippingService shippingService, EmailSenderService emailSenderService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.shippingService = shippingService;
        this.emailSenderService = emailSenderService;
        this.productService = productService;
    }

    @Transactional
    public void handleSessionCompleted(Event event) {
        Session session = deserializeSession(event);
        if (session == null) return;

        String orderId = session.getMetadata() != null ? session.getMetadata().get("orderId") : null;
        if (orderId == null) {
            log.warn("Session {} has no orderId in metadata", session.getId());
            return;
        }

        Order order = orderRepository.findById(Long.parseLong(orderId)).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Payment payment = order.getPayment();

        String paymentStatus = session.getPaymentStatus();
        log.info("Session {} paymentStatus: {}", session.getId(), paymentStatus);

        if ("paid".equals(session.getPaymentStatus()) || "succeeded".equals(session.getStatus())) {
            payment.setStatus(PaymentStatus.APPROVED);
            payment.setPaidAt(LocalDateTime.now());
            payment.setTransactionId(session.getPaymentIntent());
            order.setStatus(OrderStatus.PAID);
            shippingService.createForOrder(order);
            log.info("Order {} marked as PAID", orderId);
        } else {
            payment.setStatus(PaymentStatus.PENDING);
            log.info("Order {} payment pending (status: {})", orderId, paymentStatus);
        }

        paymentRepository.save(payment);
        orderRepository.save(order);
    }

    @Transactional
    public void handleSessionExpired(Event event) {
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

        order.getItems().forEach(item -> productService.restoreStock(item.getProduct(), item.getQuantity()));
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
        log.info("Order {} canceled due to expired session", orderId);
    }

    private Session deserializeSession(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        Optional<StripeObject> stripeObject = deserializer.getObject();

        if (stripeObject.isPresent() && stripeObject.get() instanceof Session session) {
            return session;
        }

        try {
            StripeObject unsafeObject = deserializer.deserializeUnsafe();
            if (unsafeObject instanceof Session session) {
                return session;
            }
        } catch (Exception e) {
            log.error("Failed to deserialize Stripe session (unsafe): {}", e.getMessage());
        }

        log.error("Could not deserialize Stripe session for event {}", event.getId());
        return null;
    }
}