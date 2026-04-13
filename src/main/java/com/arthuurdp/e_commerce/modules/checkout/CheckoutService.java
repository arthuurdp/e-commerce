package com.arthuurdp.e_commerce.modules.checkout;

import com.arthuurdp.e_commerce.modules.address.entity.Address;
import com.arthuurdp.e_commerce.modules.cart.entity.Cart;
import com.arthuurdp.e_commerce.modules.shipping.client.MelhorEnvioClient;
import com.arthuurdp.e_commerce.modules.shipping.dtos.FreightResponse;
import com.arthuurdp.e_commerce.shared.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.modules.checkout.dtos.CheckoutRequest;
import com.arthuurdp.e_commerce.modules.checkout.dtos.CheckoutResponse;
import com.arthuurdp.e_commerce.modules.order.entity.Order;
import com.arthuurdp.e_commerce.modules.order.OrderService;
import com.arthuurdp.e_commerce.modules.payment.entity.Payment;
import com.arthuurdp.e_commerce.modules.address.AddressRepository;
import com.arthuurdp.e_commerce.modules.cart.CartRepository;
import com.arthuurdp.e_commerce.modules.payment.PaymentService;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CheckoutService {
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final MelhorEnvioClient melhorEnvioClient;

    @Value("${melhorenvio.from-postal-code}")
    private String fromPostalCode;

    public CheckoutService(
            CartRepository cartRepository,
            AddressRepository addressRepository,
            OrderService orderService,
            PaymentService paymentService,
            MelhorEnvioClient melhorEnvioClient
    ) {
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.melhorEnvioClient = melhorEnvioClient;
    }

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest req, User user) throws StripeException {
        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.isEmpty()) {
            throw new BadRequestException("Cannot checkout with an empty cart");
        }

        Address address = addressRepository.findByIdAndUserId(req.addressId(), user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        String toPostalCode = address.getPostalCode().replaceAll("\\D", "");
        int itemCount = cart.getItems().size();
        List<MelhorEnvioClient.FreightOption> freightOptions = melhorEnvioClient.calculate(toPostalCode, itemCount);

        MelhorEnvioClient.FreightOption selectedFreight = freightOptions.stream()
                .filter(f -> f.id() == req.freightServiceId())
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Invalid freight service selected"));

        BigDecimal freightPrice = selectedFreight.price();
        BigDecimal totalWithFreight = cart.total().add(freightPrice);

        Order order = orderService.createOrder(user, address, cart, totalWithFreight);
        Payment payment = paymentService.createPayment(order, req.paymentMethod());

        Session session = paymentService.createStripeSession(
                order, user, cart, req.paymentMethod(),
                freightPrice, selectedFreight.name()
        );

        paymentService.updateTransactionId(payment, session.getId());
        
        return new CheckoutResponse(order.getId(), session.getId(), session.getUrl());
    }
}