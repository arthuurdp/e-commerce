package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.CartItem;
import com.arthuurdp.e_commerce.entities.Product;
import com.arthuurdp.e_commerce.entities.ShoppingCart;
import com.arthuurdp.e_commerce.entities.dtos.cart_item.CartItemResponse;
import com.arthuurdp.e_commerce.exceptions.ProductOutOfStockException;
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

    @Transactional
    public Page<CartItemResponse> findAllItems(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cartItemRepository.findByCartId(id, pageable).map(entityMapperService::toCartItemResponse);
    }

    public void removeAllItems(Long cartId) {
        ShoppingCart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cartItemRepository.deleteByCartId(cartId);
    }

    @Transactional
    public CartItemResponse addItem(Long cartId, Long productId) {
        ShoppingCart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductId(cartId, productId);

        if (existing.isPresent()) {
            CartItem item = existing.get();

            if (item.getQuantity() < product.getStock()) {
                item.incrementQuantity();
            } else {
                throw new ProductOutOfStockException("Product is out of stock");
            }

            return entityMapperService.toCartItemResponse(cartItemRepository.save(item));
        }

        CartItem item = new CartItem(
                cart,
                product,
                1
        );

        return entityMapperService.toCartItemResponse(cartItemRepository.save(item));
    }

    @Transactional
    public CartItemResponse removeItem(Long cartId, Long productId) {
        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId).orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        if (item.getQuantity() <= 1) {
            cartItemRepository.delete(item);
        } else {
            item.decrementQuantity();
        }
        return entityMapperService.toCartItemResponse(item);
    }

}
