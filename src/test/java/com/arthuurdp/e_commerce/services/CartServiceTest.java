package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.dtos.cart.CartItemResponse;
import com.arthuurdp.e_commerce.domain.dtos.cart.CartResponse;
import com.arthuurdp.e_commerce.domain.entities.*;
import com.arthuurdp.e_commerce.domain.enums.Gender;
import com.arthuurdp.e_commerce.domain.enums.Role;
import com.arthuurdp.e_commerce.exceptions.AuthenticationException;
import com.arthuurdp.e_commerce.exceptions.ProductOutOfStockException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CartItemRepository;
import com.arthuurdp.e_commerce.repositories.CartRepository;
import com.arthuurdp.e_commerce.repositories.ProductRepository;
import com.arthuurdp.e_commerce.services.mappers.CartMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartMapper mapper;

    @InjectMocks
    private CartService cartService;

    private User buildUser(boolean emailVerified) {
        User user = new User(
                "John", "Doe", "john@example.com",
                "encoded_secret", "52998224725",
                "11987654321", LocalDate.of(1995, 6, 15),
                Gender.MALE, Role.ROLE_USER
        );
        user.setEmailVerified(emailVerified);

        Cart cart = new Cart();
        cart.setId(1L);
        user.setCart(cart);

        return user;
    }

    private Product buildProduct(Long id, int stock) {
        Product product = new Product(
                "Product " + id, "Description", BigDecimal.valueOf(100),
                stock, 0.5, 15, 10, 20
        );
        product.setId(id);
        return product;
    }

    private Cart buildCart(Long id) {
        Cart cart = new Cart();
        cart.setId(id);
        return cart;
    }

    @Nested
    @DisplayName("display()")
    class Display {
        @Test
        @DisplayName("returns CartResponse for the user's cart")
        void shouldReturnCartResponse() {
            User user = buildUser(true);
            Cart cart = buildCart(1L);
            CartResponse expected = new CartResponse(1L, 0, List.of(), BigDecimal.ZERO);

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(mapper.toCartResponse(cart)).thenReturn(expected);

            CartResponse response = cartService.display(user);

            assertThat(response).isEqualTo(expected);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when cart does not exist")
        void shouldThrowWhenCartNotFound() {
            User user = buildUser(true);

            when(cartRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cartService.display(user)).isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("addProduct()")
    class AddProduct {
        @Test
        @DisplayName("adds a new product to the cart and returns CartItemResponse")
        void shouldAddProductSuccessfully() {
            User user = buildUser(true);
            Cart cart = buildCart(1L);
            Product product = buildProduct(10L, 5);

            CartItemResponse expected = new CartItemResponse(
                    10L, null, "Product 10", BigDecimal.valueOf(100), 1, BigDecimal.valueOf(100)
            );

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(10L)).thenReturn(Optional.of(product));
            when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));
            when(mapper.toCartItemResponse(any(CartItem.class))).thenReturn(expected);

            CartItemResponse response = cartService.addProduct(10L, user);

            assertThat(response).isEqualTo(expected);
            verify(cartItemRepository).save(any(CartItem.class));
        }

        @Test
        @DisplayName("throws AuthenticationException when email is not verified")
        void shouldThrowWhenEmailNotVerified() {
            User user = buildUser(false);

            assertThatThrownBy(() -> cartService.addProduct(10L, user)).isInstanceOf(AuthenticationException.class);

            verify(cartRepository, never()).findById(any());
            verify(productRepository, never()).findById(any());
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when product does not exist")
        void shouldThrowWhenProductNotFound() {
            User user = buildUser(true);
            Cart cart = buildCart(1L);

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cartService.addProduct(99L, user)).isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("throws ProductOutOfStockException when product stock is 0")
        void shouldThrowWhenProductOutOfStock() {
            User user = buildUser(true);
            Cart cart = buildCart(1L);
            Product product = buildProduct(10L, 0);

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(10L)).thenReturn(Optional.of(product));

            assertThatThrownBy(() -> cartService.addProduct(10L, user)).isInstanceOf(ProductOutOfStockException.class);

            verify(cartItemRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws ProductOutOfStockException when quantity in cart reaches stock limit")
        void shouldThrowWhenCartQuantityReachesStockLimit() {
            User user = buildUser(true);
            Cart cart = buildCart(1L);
            Product product = buildProduct(10L, 1);

            CartItem existingItem = new CartItem(cart, product, 1);
            cart.getItems().add(existingItem);

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(10L)).thenReturn(Optional.of(product));

            assertThatThrownBy(() -> cartService.addProduct(10L, user)).isInstanceOf(ProductOutOfStockException.class);
        }
    }

    @Nested
    @DisplayName("removeProduct()")
    class RemoveProduct {

        @Test
        @DisplayName("decrements quantity and returns updated CartItemResponse when quantity > 1")
        void shouldDecrementQuantityWhenAboveOne() {
            User user = buildUser(true);
            Cart cart = buildCart(1L);
            Product product = buildProduct(10L, 5);

            CartItem existingItem = new CartItem(cart, product, 2);
            cart.getItems().add(existingItem);

            CartItemResponse expected = new CartItemResponse(
                    10L, null, "Product 10", BigDecimal.valueOf(100), 1, BigDecimal.valueOf(100)
            );

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(10L)).thenReturn(Optional.of(product));
            when(mapper.toCartItemResponse(any(CartItem.class))).thenReturn(expected);

            var response = cartService.removeProduct(10L, user);

            assertThat(response).isPresent();
            assertThat(response.get()).isEqualTo(expected);
        }

        @Test
        @DisplayName("removes item entirely and returns empty Optional when quantity is 1")
        void shouldRemoveItemWhenQuantityIsOne() {
            User user = buildUser(true);
            Cart cart = buildCart(1L);
            Product product = buildProduct(10L, 5);

            CartItem existingItem = new CartItem(cart, product, 1);
            cart.getItems().add(existingItem);

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(10L)).thenReturn(Optional.of(product));

            var response = cartService.removeProduct(10L, user);

            assertThat(response).isEmpty();
            assertThat(cart.getItems()).doesNotContain(existingItem);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when product is not in the cart")
        void shouldThrowWhenProductNotInCart() {
            User user = buildUser(true);
            Cart cart = buildCart(1L); // empty cart
            Product product = buildProduct(10L, 5);

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(10L)).thenReturn(Optional.of(product));

            assertThatThrownBy(() -> cartService.removeProduct(10L, user))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Item not found");
        }
    }

    @Nested
    @DisplayName("clear()")
    class Clear {
        @Test
        @DisplayName("removes all items from the cart")
        void shouldClearAllItems() {
            User user = buildUser(true);
            Cart cart = buildCart(1L);
            Product product = buildProduct(10L, 5);

            cart.getItems().add(new CartItem(cart, product, 2));

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

            cartService.clear(user);

            assertThat(cart.getItems()).isEmpty();
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when cart does not exist")
        void shouldThrowWhenCartNotFound() {
            User user = buildUser(true);

            when(cartRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cartService.clear(user)).isInstanceOf(ResourceNotFoundException.class);
        }
    }
}