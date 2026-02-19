package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.CartItem;
import com.arthuurdp.e_commerce.entities.Product;
import com.arthuurdp.e_commerce.entities.ShoppingCart;
import com.arthuurdp.e_commerce.entities.cart_item.CartItemResponse;
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
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final EntityMapperService entityMapperService;

    public CartItemService(CartItemRepository cartItemRepository, CartRepository cartRepository, ProductRepository productRepository, EntityMapperService entityMapperService) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.entityMapperService = entityMapperService;
    }

    public Page<CartItemResponse> findAllItems(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cartItemRepository.findByCartId(id, pageable).map(entityMapperService::toCartItemResponse);
    }

    @Transactional
    public CartItemResponse addItem(Long cartId, Long productId, int quantity) {
        ShoppingCart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductId(cartId, productId);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            return entityMapperService.toCartItemResponse(cartItemRepository.save(item));
        }

        CartItem item = new CartItem(
                cart,
                product,
                quantity
        );

        return entityMapperService.toCartItemResponse(cartItemRepository.save(item));
    }

}
