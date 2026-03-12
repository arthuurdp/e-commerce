package com.arthuurdp.e_commerce.modules.checkout;

import com.arthuurdp.e_commerce.modules.address.entity.Address;
import com.arthuurdp.e_commerce.modules.cart.entity.Cart;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderService orderService;
    private final PaymentService paymentService;

    public CheckoutService(CartRepository cartRepository, AddressRepository addressRepository, OrderService orderService, PaymentService paymentService) {
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest req, User user) throws StripeException {
        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.isEmpty()) {
            throw new BadRequestException("Cannot checkout with an empty cart");
        }

        Address address = addressRepository.findByIdAndUserId(req.addressId(), user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Order order = orderService.createOrder(user, address, cart);
        Payment payment = paymentService.createPayment(order, req.paymentMethod());
        order.setPayment(payment);

        Session session = paymentService.createStripeSession(order, user, cart, req.paymentMethod());

        paymentService.updateTransactionId(payment, session.getId());

        cart.clear();
        cartRepository.save(cart);

        return new CheckoutResponse(
                order.getId(),
                session.getId(),
                session.getUrl()
        );
    }
}