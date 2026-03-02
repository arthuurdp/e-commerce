package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.*;
import com.arthuurdp.e_commerce.entities.dtos.order.OrderDetailsResponse;
import com.arthuurdp.e_commerce.entities.dtos.order.OrderResponse;
import com.arthuurdp.e_commerce.exceptions.AccessDeniedException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EntityMapperService entityMapperService;
    private final AuthService authService;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, EntityMapperService entityMapperService, AuthService authService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.entityMapperService = entityMapperService;
        this.authService = authService;
    }

    @Transactional
    public OrderDetailsResponse findById(Long id) {
        User user = authService.getCurrentUser();
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getEmail().equals(user.getEmail())) {
            throw new AccessDeniedException("Access denied");
        }

        return entityMapperService.toOrderDetailsResponse(order);
    }

    @Transactional
    public List<OrderResponse> findByUser() {
        User user = authService.getCurrentUser();
        return orderRepository.findByUserId(user.getId()).stream().map(entityMapperService::toOrderResponse).toList();
    }
}
