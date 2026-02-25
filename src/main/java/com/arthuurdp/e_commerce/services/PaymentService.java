package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.dtos.payment.PaymentRequest;
import com.arthuurdp.e_commerce.entities.dtos.payment.PaymentResponse;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final EntityMapperService mapper;

    public PaymentService(EntityMapperService mapper) {
        this.mapper = mapper;
    }

    public PaymentResponse createPayment(PaymentRequest req) throws MPException, MPApiException {
        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .transactionAmount(req.amount())
                .description(req.description())
                .paymentMethodId(mapper.toMercadoPagoPaymentMethodId(req.paymentMethod()))
                .payer(PaymentPayerRequest.builder()
                        .email(req.payerEmail())
                        .build())
                .build();

        PaymentClient client = new PaymentClient();
        Payment payment = client.create(request);

        String qrCode = null;
        String qrCodeBase64 = null;
        String boletoUrl = null;

        if (payment.getPointOfInteraction() != null) {
            var transaction = payment.getPointOfInteraction().getTransactionData();
            qrCode = transaction.getQrCode();
            qrCodeBase64 = transaction.getQrCodeBase64();
        }
        if (payment.getTransactionDetails() != null) {
            boletoUrl = payment.getTransactionDetails().getExternalResourceUrl();
        }

        return new PaymentResponse(
                payment.getId(),
                mapper.fromMercadoPagoStatus(payment.getStatus()),
                payment.getStatusDetail(),
                qrCode,
                qrCodeBase64,
                boletoUrl
        );
    }
}

