package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.modules.address.entity.Address;
import com.arthuurdp.e_commerce.modules.cart.entity.Cart;
import com.arthuurdp.e_commerce.modules.cart.entity.CartItem;
import com.arthuurdp.e_commerce.modules.checkout.dtos.CheckoutRequest;
import com.arthuurdp.e_commerce.modules.checkout.dtos.CheckoutResponse;
import com.arthuurdp.e_commerce.shared.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.modules.user.enums.Gender;
import com.arthuurdp.e_commerce.modules.payment.enums.PaymentMethod;
import com.arthuurdp.e_commerce.modules.user.enums.Role;
import com.arthuurdp.e_commerce.modules.order.entity.Order;
import com.arthuurdp.e_commerce.modules.payment.entity.Payment;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.modules.checkout.CheckoutService;
import com.arthuurdp.e_commerce.modules.order.OrderService;
import com.arthuurdp.e_commerce.modules.payment.PaymentService;
import com.arthuurdp.e_commerce.modules.address.AddressRepository;
import com.arthuurdp.e_commerce.modules.cart.CartRepository;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private OrderService orderService;
    @Mock private PaymentService paymentService;

    @InjectMocks
    private CheckoutService checkoutService;

    private User user;
    private Cart cart;
    private Address address;
    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setId(1L);

        user = new User(
                "John", "Doe", "john@example.com", "encoded",
                "52998224725", "11987654321",
                LocalDate.of(1995, 6, 15), Gender.MALE, Role.ROLE_USER
        );
        user.setId(1L);
        user.setCart(cart);

        address = new Address("Home", "Main St", 100, "Apt 1", "Downtown", "01310100");
        address.setId(10L);

        order = new Order();
        order.setId(99L);
        order.setTotal(BigDecimal.valueOf(2500));

        payment = new Payment();
        payment.setId(5L);
    }

    @Nested
    @DisplayName("checkout()")
    class Checkout {

        @Test
        @DisplayName("completes checkout and returns CheckoutResponse")
        void shouldCompleteCheckoutSuccessfully() throws StripeException {
            cart.getItems().add(new CartItem(cart, buildProduct(), 1));

            CheckoutRequest req = new CheckoutRequest(10L, PaymentMethod.CREDIT_CARD);

            Session session = mock(Session.class);
            when(session.getId()).thenReturn("sess_123");
            when(session.getUrl()).thenReturn("https://stripe.com/pay/sess_123");

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(addressRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(address));
            when(orderService.createOrder(user, address, cart)).thenReturn(order);
            when(paymentService.createPayment(order, PaymentMethod.CREDIT_CARD)).thenReturn(payment);
            when(paymentService.createStripeSession(order, user, cart, PaymentMethod.CREDIT_CARD)).thenReturn(session);

            CheckoutResponse response = checkoutService.checkout(req, user);

            assertThat(response.orderId()).isEqualTo(99L);
            assertThat(response.preferenceId()).isEqualTo("sess_123");
            assertThat(response.initPoint()).isEqualTo("https://stripe.com/pay/sess_123");
        }

        @Test
        @DisplayName("clears cart and saves it after checkout")
        void shouldClearCartAfterCheckout() throws StripeException {
            cart.getItems().add(new CartItem(cart, buildProduct(), 1));

            CheckoutRequest req = new CheckoutRequest(10L, PaymentMethod.PIX);

            Session session = mock(Session.class);
            when(session.getId()).thenReturn("sess_456");
            when(session.getUrl()).thenReturn("https://stripe.com/pay/sess_456");

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(addressRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(address));
            when(orderService.createOrder(any(), any(), any())).thenReturn(order);
            when(paymentService.createPayment(any(), any())).thenReturn(payment);
            when(paymentService.createStripeSession(any(), any(), any(), any())).thenReturn(session);

            checkoutService.checkout(req, user);

            assertThat(cart.getItems()).isEmpty();
            verify(cartRepository).save(cart);
        }

        @Test
        @DisplayName("sets payment on order and updates transaction id")
        void shouldSetPaymentAndUpdateTransactionId() throws StripeException {
            cart.getItems().add(new CartItem(cart, buildProduct(), 1));

            CheckoutRequest req = new CheckoutRequest(10L, PaymentMethod.BOLETO);

            Session session = mock(Session.class);
            when(session.getId()).thenReturn("sess_789");
            when(session.getUrl()).thenReturn("https://stripe.com/pay/sess_789");

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(addressRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(address));
            when(orderService.createOrder(any(), any(), any())).thenReturn(order);
            when(paymentService.createPayment(order, PaymentMethod.BOLETO)).thenReturn(payment);
            when(paymentService.createStripeSession(any(), any(), any(), any())).thenReturn(session);

            checkoutService.checkout(req, user);

            assertThat(order.getPayment()).isEqualTo(payment);
            verify(paymentService).updateTransactionId(payment, "sess_789");
        }

        @Test
        @DisplayName("throws BadRequestException when cart is empty")
        void shouldThrowWhenCartIsEmpty() {
            CheckoutRequest req = new CheckoutRequest(10L, PaymentMethod.CREDIT_CARD);

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

            assertThatThrownBy(() -> checkoutService.checkout(req, user)).isInstanceOf(BadRequestException.class).hasMessage("Cannot checkout with an empty cart");

            verifyNoInteractions(addressRepository, orderService, paymentService);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when cart does not exist")
        void shouldThrowWhenCartNotFound() {
            when(cartRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> checkoutService.checkout(new CheckoutRequest(10L, PaymentMethod.CREDIT_CARD), user)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Cart not found");

            verifyNoInteractions(addressRepository, orderService, paymentService);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when address does not belong to user")
        void shouldThrowWhenAddressNotFound() {
            cart.getItems().add(new CartItem(cart, buildProduct(), 1));

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(addressRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> checkoutService.checkout(new CheckoutRequest(10L, PaymentMethod.CREDIT_CARD), user))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Address not found");

            verifyNoInteractions(orderService, paymentService);
        }
    }

    private Product buildProduct() {
        Product product = new Product(
                "Laptop", "A great laptop", BigDecimal.valueOf(2500),
                5, 1.5, 30, 20, 5
        );
        product.setId(1L);
        return product;
    }
}