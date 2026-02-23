package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.CartItem;
import com.arthuurdp.e_commerce.entities.Product;
import com.arthuurdp.e_commerce.entities.Cart;
import com.arthuurdp.e_commerce.entities.User;
import com.arthuurdp.e_commerce.entities.dtos.cart.CartItemResponse;
import com.arthuurdp.e_commerce.entities.dtos.cart.CartResponse;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CartItemRepository;
import com.arthuurdp.e_commerce.repositories.CartRepository;
import com.arthuurdp.e_commerce.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;
    private final EntityMapperService entityMapperService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, AuthService authService, EntityMapperService entityMapperService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.authService = authService;
        this.entityMapperService = entityMapperService;
    }

    @Transactional
    public CartResponse findById() {
        User user = authService.getCurrentUser();
        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        return entityMapperService.toCartResponse(cart);
    }

    @Transactional
    public CartItemResponse addProduct(Long productId) {
        User user = authService.getCurrentUser();
        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        CartItem item = cart.addProduct(product);

        cartItemRepository.save(item);

        return entityMapperService.toCartItemResponse(item);
    }

    @Transactional
    public Optional<CartItemResponse> removeProduct(Long productId) {
        User user = authService.getCurrentUser();
        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return cart.removeProduct(product).map(entityMapperService::toCartItemResponse);
    }

    @Transactional
    public void removeAllItems() {
        User user = authService.getCurrentUser();
        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.clear();
    }
}
