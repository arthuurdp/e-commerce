package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.*;
import com.arthuurdp.e_commerce.entities.dtos.checkout.CheckoutRequest;
import com.arthuurdp.e_commerce.entities.dtos.checkout.CheckoutResponse;
import com.arthuurdp.e_commerce.entities.enums.OrderStatus;
import com.arthuurdp.e_commerce.entities.enums.PaymentMethod;
import com.arthuurdp.e_commerce.entities.enums.PaymentStatus;
import com.arthuurdp.e_commerce.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.*;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CheckoutService {

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.failure-url}")
    private String failureUrl;

    private final AuthService authService;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;

    public CheckoutService(
            AuthService authService,
            CartRepository cartRepository,
            AddressRepository addressRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            ProductRepository productRepository
    ) {
        this.authService = authService;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest req) throws StripeException {
        User user = authService.getCurrentUser();

        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.isEmpty()) {
            throw new BadRequestException("Cannot checkout with an empty cart");
        }

        Address address = addressRepository.findByIdAndUserId(req.addressId(), user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(cart.total());

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Product " + product.getName() + " has insufficient stock.");
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

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(req.paymentMethod());
        payment.setAmount(cart.total());
        payment.setStatus(PaymentStatus.PENDING);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

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

        List<SessionCreateParams.PaymentMethodType> paymentMethods = resolvePaymentMethods(req.paymentMethod());

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCustomerEmail(user.getEmail())
                .addAllPaymentMethodType(paymentMethods)
                .addAllLineItem(lineItems)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(failureUrl)
                .putMetadata("orderId", savedOrder.getId().toString())
                .build();

        Session session = Session.create(params);

        payment.setTransactionId(session.getId());
        paymentRepository.save(payment);

        cart.clear();
        cartRepository.save(cart);

        return new CheckoutResponse(
                savedOrder.getId(),
                session.getId(),
                session.getUrl()
        );
    }

    private List<SessionCreateParams.PaymentMethodType> resolvePaymentMethods(PaymentMethod method) {
        return switch (method) {
            case CREDIT_CARD -> List.of(SessionCreateParams.PaymentMethodType.CARD);
            case PIX -> List.of(SessionCreateParams.PaymentMethodType.PIX);
            case BOLETO -> List.of(SessionCreateParams.PaymentMethodType.BOLETO);
        };
    }
}