package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.payment.PaymentRequest;
import com.arthuurdp.e_commerce.entities.dtos.payment.PaymentResponse;
import com.arthuurdp.e_commerce.services.PaymentService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest req) throws MPException, MPApiException {
        try {
            PaymentResponse response = service.createPayment(req);
            return ResponseEntity.ok().body(response);
        } catch (MPApiException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (MPException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
