package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.CartItem;
import com.arthuurdp.e_commerce.entities.Product;
import com.arthuurdp.e_commerce.entities.ShoppingCart;
import com.arthuurdp.e_commerce.entities.dtos.cart.CartItemResponse;
import com.arthuurdp.e_commerce.entities.dtos.cart.CartResponse;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CartItemRepository;
import com.arthuurdp.e_commerce.repositories.CartRepository;
import com.arthuurdp.e_commerce.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final EntityMapperService entityMapperService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, EntityMapperService entityMapperService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.entityMapperService = entityMapperService;
    }

    @Transactional
    public CartResponse findById(Long cartId) {
        ShoppingCart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        return entityMapperService.toCartResponse(cart);
    }

    @Transactional
    public CartItemResponse addItem(Long cartId, Long productId) {
        ShoppingCart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        CartItem item = cart.addProduct(product);
        cartItemRepository.save(item);

        return entityMapperService.toCartItemResponse(item);
    }

    @Transactional
    public Optional<CartItemResponse> removeItem(Long cartId, Long productId) {
        ShoppingCart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return cart.removeProduct(product).map(entityMapperService::toCartItemResponse);
    }

    @Transactional
    public void removeAllItems(Long cartId) {
        ShoppingCart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.clear();
    }
}
