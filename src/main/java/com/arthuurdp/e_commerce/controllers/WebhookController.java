package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.Payment;
import com.arthuurdp.e_commerce.repositories.OrderRepository;
import com.arthuurdp.e_commerce.repositories.PaymentRepository;
import com.arthuurdp.e_commerce.services.EntityMapperService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final EntityMapperService entityMapperService;

    public WebhookController(PaymentRepository paymentRepository, OrderRepository orderRepository, EntityMapperService entityMapperService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.entityMapperService = entityMapperService;
    }

    @PostMapping("/mercadopago")
    public ResponseEntity<Void> handleNotification(@RequestBody Map<String, Object> payload, @RequestHeader("x-signature") String signature) throws MPException, MPApiException {
        String topic = (String) payload.get("type");

        if ("payment".equals(topic)) {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            String paymentId = data.get("id").toString();

            PaymentClient paymentClient = new PaymentClient();
            com.mercadopago.resources.payment.Payment mpPayment = paymentClient.get(Long.parseLong(paymentId));

            String orderId = mpPayment.getExternalReference();

            orderRepository.findById(Long.parseLong(orderId)).ifPresent(order -> {
                Payment payment = order.getPayment();
                payment.setStatus(entityMapperService.fromMercadoPagoStatus(mpPayment.getStatus()));
                if ("approved".equals(mpPayment.getStatus())) {
                    payment.setPaidAt(LocalDateTime.now());
                    payment.setTransactionId(mpPayment.getId().toString());
                }
                paymentRepository.save(payment);
            });
        }
        return ResponseEntity.ok().build();
    }
}