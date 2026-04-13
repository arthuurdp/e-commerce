package com.arthuurdp.e_commerce.modules.favorite.service;

import com.arthuurdp.e_commerce.modules.product.ProductRepository;
import com.arthuurdp.e_commerce.modules.product.dtos.ProductResponse;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.modules.product.mapper.ProductMapper;
import com.arthuurdp.e_commerce.modules.user.UserRepository;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public FavoriteService(UserRepository userRepository, ProductRepository productRepository, ProductMapper productMapper) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public void addFavorite(Long productId, User user) {
        User targetUser = userRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        targetUser.getFavoriteProducts().add(product);
        userRepository.save(targetUser);
    }

    @Transactional
    public void removeFavorite(Long productId, User user) {
        User targetUser = userRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        targetUser.getFavoriteProducts().remove(product);
        userRepository.save(targetUser);
    }

    @Transactional(readOnly = true)
    public Set<ProductResponse> getUserFavorites(User user) {
        User targetUser = userRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return targetUser.getFavoriteProducts().stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toSet());
    }
}