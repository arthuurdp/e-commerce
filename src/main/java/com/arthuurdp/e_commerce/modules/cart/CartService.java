package com.arthuurdp.e_commerce.modules.cart;

import com.arthuurdp.e_commerce.modules.cart.entity.CartItem;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.modules.cart.entity.Cart;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.modules.cart.dtos.CartItemResponse;
import com.arthuurdp.e_commerce.modules.cart.dtos.CartResponse;
import com.arthuurdp.e_commerce.shared.exceptions.AuthenticationException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.modules.product.ProductRepository;
import com.arthuurdp.e_commerce.modules.cart.mapper.CartMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartMapper mapper;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, CartMapper mapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Transactional
    public CartResponse display(User user) {
        return mapper.toCartResponse(cartRepository.findById(user.getCart().getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found")));
    }

    @Transactional
    public CartItemResponse addProduct(Long productId, User user) {
        if (!user.isEmailVerified()) {
            throw new AuthenticationException("Please verify your email before adding products to the cart");
        }

        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        CartItem item = cart.addProduct(product);

        cartItemRepository.save(item);

        return mapper.toCartItemResponse(item);
    }

    @Transactional
    public Optional<CartItemResponse> removeProduct(Long productId, User user) {
        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return cart.removeProduct(product).map(mapper::toCartItemResponse);
    }

    @Transactional
    public void clear(User user) {
        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.clear();
    }
}
