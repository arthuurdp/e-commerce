package com.arthuurdp.e_commerce.modules.myactivity.favorite;

import com.arthuurdp.e_commerce.modules.myactivity.favorite.entity.Favorite;
import com.arthuurdp.e_commerce.modules.product.ProductRepository;
import com.arthuurdp.e_commerce.modules.product.dtos.ProductResponse;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.modules.product.mapper.ProductMapper;
import com.arthuurdp.e_commerce.modules.notification.NotificationService;
import com.arthuurdp.e_commerce.modules.user.UserRepository;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.shared.exceptions.ConflictException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final NotificationService notificationService;

    public FavoriteService(FavoriteRepository favoriteRepository, ProductRepository productRepository, ProductMapper productMapper, NotificationService notificationService) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.notificationService = notificationService;
    }

    @Transactional
    public Map<String, String> addFavorite(Long productId, User user) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (favoriteRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new ConflictException("Product is already in your favorites");
        }

        favoriteRepository.save(new Favorite(user, product));
        notificationService.createNotification(user, "You have added " + product.getName() + " to your favorites!");

        return Map.of("message", "Product added to favorites!");
    }

    @Transactional
    public void removeFavorite(Long productId, User user) {
        if (!favoriteRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new ResourceNotFoundException("Product not found");
        }
        favoriteRepository.deleteByUserIdAndProductId(user.getId(), productId);
    }

    @Transactional(readOnly = true)
    public Set<ProductResponse> getUserFavorites(User user) {
        return favoriteRepository.findByUserId(user.getId()).stream()
                .map(Favorite::getProduct)
                .map(productMapper::toProductResponse)
                .collect(Collectors.toSet());
    }
}