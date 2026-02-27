package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.Order;
import com.arthuurdp.e_commerce.entities.Payment;
import com.arthuurdp.e_commerce.entities.dtos.CheckoutRequest;
import com.arthuurdp.e_commerce.entities.dtos.CheckoutResponse;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.OrderRepository;
import com.arthuurdp.e_commerce.repositories.PaymentRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MercadoPagoService {
    @Value("${mercadopago.success-url}")
    private String successUrl;

    @Value("${mercadopago.failure-url}")
    private String failureUrl;

    @Value("${mercadopago.pending-url}")
    private String pendingUrl;

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public MercadoPagoService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public CheckoutResponse createPreference(CheckoutRequest req) throws MPException, MPApiException {
        try {
            PreferenceClient client = new PreferenceClient();

            Order order = orderRepository.findById(req.orderId()).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

            // Monta os itens
            List<PreferenceItemRequest> items = req.items().stream()
                    .map(item -> PreferenceItemRequest.builder()
                            .title(item.title())
                            .quantity(item.quantity())
                            .unitPrice(item.unitPrice())
                            .currencyId(item.currencyId())
                            .build())
                    .toList();

            // Payer
            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .email(req.payerEmail())
                    .build();

            // URLs de retorno
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(successUrl)
                    .failure(failureUrl)
                    .pending(pendingUrl)
                    .build();

            // Monta a preferência
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .backUrls(backUrls)
                    .autoReturn("approved") // redireciona automaticamente em pagamento aprovado
                    .externalReference(order.getId().toString())
                    .build();

            Preference preference = client.create(preferenceRequest);

            Payment payment = order.getPayment();
            payment.setTransactionId(preference.getId());
            payment.setMethod(req.paymentMethod());
            payment.setAmount(order.getTotal());

            paymentRepository.save(payment);

            return new CheckoutResponse(
                    preference.getId(),
                    preference.getInitPoint(),
                    preference.getSandboxInitPoint()
            );
        } catch (MPApiException e) {
            System.out.println("Status: " + e.getStatusCode());
            System.out.println("Response: " + e.getApiResponse().getContent());
            throw e;
        }
    }
}
