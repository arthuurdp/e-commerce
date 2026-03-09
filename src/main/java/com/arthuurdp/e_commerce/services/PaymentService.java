package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.entities.Cart;
import com.arthuurdp.e_commerce.domain.entities.Order;
import com.arthuurdp.e_commerce.domain.entities.Payment;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.domain.enums.PaymentMethod;
import com.arthuurdp.e_commerce.domain.enums.PaymentStatus;
import com.arthuurdp.e_commerce.repositories.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {
    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.failure-url}")
    private String failureUrl;

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment createPayment(Order order, PaymentMethod method) {
        Payment payment = new Payment(
                order,
                method,
                order.getTotal()
      );
        return paymentRepository.save(payment);
    }

    public Session createStripeSession(Order order, User user, Cart cart, PaymentMethod method) throws StripeException {
        List<SessionCreateParams.LineItem> lineItems = cart.getItems().stream()
                .map(item -> SessionCreateParams.LineItem.builder()
                        .setQuantity((long) item.getQuantity())
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("brl")
                                .setUnitAmount(item.getProduct().getPrice()
                                        .multiply(BigDecimal.valueOf(100))
                                        .longValue())
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(item.getProduct().getName())
                                        .setDescription(item.getProduct().getDescription())
                                        .addImage(item.getProduct().getMainImageUrl())
                                        .build())
                                .build())
                        .build())
                .toList();

        List<SessionCreateParams.PaymentMethodType> paymentMethods = resolvePaymentMethods(method);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCustomerEmail(user.getEmail())
                .addAllPaymentMethodType(paymentMethods)
                .addAllLineItem(lineItems)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(failureUrl)
                .putMetadata("orderId", order.getId().toString())
                .build();

        return Session.create(params);
    }

    @Transactional
    public void updateTransactionId(Payment payment, String transactionId) {
        payment.setTransactionId(transactionId);
        paymentRepository.save(payment);
    }

    private List<SessionCreateParams.PaymentMethodType> resolvePaymentMethods(PaymentMethod method) {
        return switch (method) {
            case CREDIT_CARD -> List.of(SessionCreateParams.PaymentMethodType.CARD);
            case PIX -> List.of(SessionCreateParams.PaymentMethodType.PIX);
            case BOLETO -> List.of(SessionCreateParams.PaymentMethodType.BOLETO);
        };
    }
}
