package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.*;
import com.arthuurdp.e_commerce.entities.dtos.checkout.CheckoutRequest;
import com.arthuurdp.e_commerce.entities.dtos.checkout.CheckoutResponse;
import com.arthuurdp.e_commerce.entities.enums.OrderStatus;
import com.arthuurdp.e_commerce.entities.enums.PaymentMethod;
import com.arthuurdp.e_commerce.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.exceptions.PaymentFailedException;
import com.arthuurdp.e_commerce.exceptions.ProductOutOfStockException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.*;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class CheckoutService {

    private final AuthService authService;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final EntityMapperService entityMapperService;

    @Value("${mercadopago.notification-url:}")
    private String notificationUrl;

    public CheckoutService(
            AuthService authService,
            CartRepository cartRepository,
            AddressRepository addressRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            ProductRepository productRepository,
            EntityMapperService entityMapperService
    ) {
        this.authService = authService;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.entityMapperService = entityMapperService;
    }

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest req) {
        User user = authService.getCurrentUser();

        // Validate payment method — credit card not yet supported
        if (req.paymentMethod() == PaymentMethod.CREDIT_CARD) {
            throw new BadRequestException("Credit card payment is not yet supported. Please use PIX or BOLETO.");
        }

        // Load cart with items
        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.isEmpty()) {
            throw new BadRequestException("Cannot checkout with an empty cart");
        }

        // Load address (must belong to user)
        Address address = addressRepository.findByIdAndUserId(req.addressId(), user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        // Create Order
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(cart.total());

        // Create OrderItems from cart and reduce stock
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new ProductOutOfStockException(
                        "Product '" + product.getName() + "' is out of stock"
                );
            }

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            order.getItems().add(orderItem);
        }

        // Save order (Payment is auto-created via @PrePersist on Order)
        Order savedOrder = orderRepository.save(order);

        // Call Mercado Pago to create the payment
        Payment mpPayment;
        try {
            mpPayment = createMercadoPagoPayment(user, savedOrder, req.paymentMethod());
        } catch (MPApiException | MPException e) {
            order.setStatus(OrderStatus.CANCELED);
            throw new PaymentFailedException("Payment creation failed: " + e.getMessage(), e);
        }

        // Update Payment entity with Mercado Pago response
        com.arthuurdp.e_commerce.entities.Payment payment = savedOrder.getPayment();
        payment.setOrder(savedOrder);
        payment.setMethod(req.paymentMethod());
        payment.setAmount(savedOrder.getTotal());
        payment.setTransactionId(String.valueOf(mpPayment.getId()));
        payment.setStatus(entityMapperService.fromMercadoPagoStatus(mpPayment.getStatus()));
        paymentRepository.save(payment);

        cart.clear();
        cartRepository.save(cart);

        String pixQrCode = null;
        String pixQrCodeBase64 = null;
        String boletoUrl = null;

        if (mpPayment.getPointOfInteraction() != null
                && mpPayment.getPointOfInteraction().getTransactionData() != null) {
            var txData = mpPayment.getPointOfInteraction().getTransactionData();
            pixQrCode = txData.getQrCode();
            pixQrCodeBase64 = txData.getQrCodeBase64();
        }

        if (mpPayment.getTransactionDetails() != null) {
            boletoUrl = mpPayment.getTransactionDetails().getExternalResourceUrl();
        }

        return new CheckoutResponse(
                savedOrder.getId(),
                savedOrder.getStatus(),
                savedOrder.getTotal(),
                savedOrder.getCreatedAt(),
                payment.getId(),
                payment.getMethod(),
                payment.getStatus(),
                pixQrCode,
                pixQrCodeBase64,
                boletoUrl
        );
    }

    private Payment createMercadoPagoPayment(User user, Order order, PaymentMethod method) throws MPException, MPApiException {

        String paymentMethodId = switch (method) {
            case PIX -> "pix";
            case BOLETO -> "bolbradesco";
            case CREDIT_CARD -> throw new BadRequestException("Credit card not supported yet");
        };

        PaymentCreateRequest.PaymentCreateRequestBuilder builder = PaymentCreateRequest.builder()
                .transactionAmount(order.getTotal())
                .description("Order #" + order.getId())
                .paymentMethodId(paymentMethodId)
                .payer(PaymentPayerRequest.builder()
                        .email(user.getEmail())
                        .build());

        // Only attach notification URL if configured
        if (notificationUrl != null && !notificationUrl.isBlank()) {
            builder.notificationUrl(notificationUrl);
        }

        PaymentClient client = new PaymentClient();
        return client.create(builder.build());
    }
}
