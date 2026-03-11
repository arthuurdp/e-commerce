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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CartMapper mapper;

    @InjectMocks
    private CartService cartService;

    private User verifiedUser;
    private User unverifiedUser;
    private Cart cart;
    private Product productInStock;
    private Product productOutOfStock;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setId(1L);

        verifiedUser = new User(
                "Arthur", "Test", "arthur@test.com",
                "senha", "52998224725", "11987654321",
                LocalDate.of(1995, 6, 15), Gender.MALE, Role.ROLE_USER
        );
        verifiedUser.setEmailVerified(true);
        verifiedUser.setCart(cart);

        unverifiedUser = new User(
                "Bob", "Test", "bob@test.com",
                "senha", "52998224726", "11987654322",
                LocalDate.of(1995, 6, 15), Gender.MALE, Role.ROLE_USER
        );
        unverifiedUser.setEmailVerified(false);
        unverifiedUser.setCart(cart);

        productInStock = new Product(
                "Laptop", "A great laptop", BigDecimal.valueOf(2500),
                5, 1.5, 30, 20, 5
        );
        productInStock.setId(1L);

        productOutOfStock = new Product(
                "Sold Out Item", "No stock", BigDecimal.valueOf(100),
                0, 0.3, 10, 5, 2
        );
        productOutOfStock.setId(2L);
    }

    @Nested
    @DisplayName("display()")
    class Display {

        @Test
        @DisplayName("returns CartResponse for the user's cart")
        void shouldReturnCartResponse() {
            CartResponse expected = new CartResponse(1L, 0, List.of(), BigDecimal.ZERO);

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(mapper.toCartResponse(cart)).thenReturn(expected);

            CartResponse response = cartService.display(verifiedUser);

            assertThat(response).isEqualTo(expected);
            verify(cartRepository).findById(1L);
            verify(mapper).toCartResponse(cart);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when cart does not exist")
        void shouldThrowWhenCartNotFound() {
            when(cartRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cartService.display(verifiedUser))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Cart not found");
        }
    }

    @Nested
    @DisplayName("addProduct()")
    class AddProduct {

        @Test
        @DisplayName("adds a new product to the cart and returns CartItemResponse")
        void shouldAddProductSuccessfully() {
            CartItemResponse expected = new CartItemResponse(
                    1L, null, "Laptop", BigDecimal.valueOf(2500), 1, BigDecimal.valueOf(2500)
            );

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(1L)).thenReturn(Optional.of(productInStock));
            when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));
            when(mapper.toCartItemResponse(any(CartItem.class))).thenReturn(expected);

            CartItemResponse response = cartService.addProduct(1L, verifiedUser);

            assertThat(response).isEqualTo(expected);
            verify(cartItemRepository).save(any(CartItem.class));
        }

        @Test
        @DisplayName("throws AuthenticationException when email is not verified")
        void shouldThrowWhenEmailNotVerified() {
            assertThatThrownBy(() -> cartService.addProduct(1L, unverifiedUser)).isInstanceOf(AuthenticationException.class);

            verifyNoInteractions(cartRepository, productRepository, cartItemRepository);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when cart does not exist")
        void shouldThrowWhenCartNotFound() {
            when(cartRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cartService.addProduct(1L, verifiedUser)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Cart not found");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when product does not exist")
        void shouldThrowWhenProductNotFound() {
            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cartService.addProduct(99L, verifiedUser)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Product not found");
        }

        @Test
        @DisplayName("throws ProductOutOfStockException when product stock is 0")
        void shouldThrowWhenProductOutOfStock() {
            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(2L)).thenReturn(Optional.of(productOutOfStock));

            assertThatThrownBy(() -> cartService.addProduct(2L, verifiedUser)).isInstanceOf(ProductOutOfStockException.class);

            verify(cartItemRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws ProductOutOfStockException when cart quantity reaches stock limit")
        void shouldThrowWhenCartQuantityReachesStockLimit() {
            productInStock.setStock(1);
            cart.getItems().add(new CartItem(cart, productInStock, 1));

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(1L)).thenReturn(Optional.of(productInStock));

            assertThatThrownBy(() -> cartService.addProduct(1L, verifiedUser)).isInstanceOf(ProductOutOfStockException.class);

            verify(cartItemRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("removeProduct()")
    class RemoveProduct {

        @Test
        @DisplayName("decrements quantity and returns updated CartItemResponse when quantity > 1")
        void shouldDecrementQuantityWhenAboveOne() {
            cart.getItems().add(new CartItem(cart, productInStock, 2));

            CartItemResponse expected = new CartItemResponse(
                    1L, null, "Laptop", BigDecimal.valueOf(2500), 1, BigDecimal.valueOf(2500)
            );

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(1L)).thenReturn(Optional.of(productInStock));
            when(mapper.toCartItemResponse(any(CartItem.class))).thenReturn(expected);

            Optional<CartItemResponse> response = cartService.removeProduct(1L, verifiedUser);

            assertThat(response).isPresent();
            assertThat(response.get()).isEqualTo(expected);
        }

        @Test
        @DisplayName("removes item entirely and returns empty Optional when quantity is 1")
        void shouldRemoveItemWhenQuantityIsOne() {
            CartItem item = new CartItem(cart, productInStock, 1);
            cart.getItems().add(item);

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(1L)).thenReturn(Optional.of(productInStock));

            Optional<CartItemResponse> response = cartService.removeProduct(1L, verifiedUser);

            assertThat(response).isEmpty();
            assertThat(cart.getItems()).doesNotContain(item);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when product is not in the cart")
        void shouldThrowWhenProductNotInCart() {
            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(productRepository.findById(1L)).thenReturn(Optional.of(productInStock));

            assertThatThrownBy(() -> cartService.removeProduct(1L, verifiedUser)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Item not found");
        }
    }

    @Nested
    @DisplayName("clear()")
    class Clear {

        @Test
        @DisplayName("removes all items from the cart")
        void shouldClearAllItems() {
            cart.getItems().add(new CartItem(cart, productInStock, 2));

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

            cartService.clear(verifiedUser);

            assertThat(cart.getItems()).isEmpty();
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when cart does not exist")
        void shouldThrowWhenCartNotFound() {
            when(cartRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cartService.clear(verifiedUser)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Cart not found");
        }
    }
}