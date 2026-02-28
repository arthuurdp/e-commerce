package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.*;
import com.arthuurdp.e_commerce.entities.dtos.OrderItemResponse;
import com.arthuurdp.e_commerce.entities.dtos.OrderResponse;
import com.arthuurdp.e_commerce.entities.enums.OrderStatus;
import com.arthuurdp.e_commerce.exceptions.AccessDeniedException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EntityMapperService entityMapperService;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, EntityMapperService entityMapperService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.entityMapperService = entityMapperService;
    }

    public OrderResponse findById(Long id, String userEmail) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Access denied");
        }

        return entityMapperService.toOrderResponse(order);
    }

    public List<OrderResponse> findByUser(String userEmail) {
        User user = (User) userRepository.findByEmail(userEmail);

        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private OrderResponse toResponseDTO(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotal(),
                order.getCreatedAt(),
                items
        );
    }
}
