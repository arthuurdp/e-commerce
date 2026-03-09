package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.entities.Order;
import com.arthuurdp.e_commerce.domain.entities.Shipping;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.domain.dtos.shipping.ShippingResponse;
import com.arthuurdp.e_commerce.domain.dtos.shipping.UpdateShippingRequest;
import com.arthuurdp.e_commerce.domain.enums.OrderStatus;
import com.arthuurdp.e_commerce.domain.enums.ShippingStatus;
import com.arthuurdp.e_commerce.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.exceptions.ConflictException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.OrderRepository;
import com.arthuurdp.e_commerce.repositories.ShippingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShippingService {
    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;
    private final EntityMapperService entityMapperService;

    public ShippingService(ShippingRepository shippingRepository, OrderRepository orderRepository, EntityMapperService entityMapperService) {
        this.shippingRepository = shippingRepository;
        this.orderRepository = orderRepository;
        this.entityMapperService = entityMapperService;
    }

    @Transactional
    public ShippingResponse create(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (shippingRepository.existsByOrderId(orderId)) {
            throw new ConflictException("Shipping already exists");
        }

        return entityMapperService.toShippingResponse(shippingRepository.save(new Shipping(order)));
    }

    @Transactional
    public ShippingResponse findByOrderId(Long orderId, User user) {
        Shipping shipping = shippingRepository.findByOrderIdAndOrderUserId(orderId, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Shipping not found"));
        return entityMapperService.toShippingResponse(shipping);
    }

    @Transactional
    public ShippingResponse update(Long shippingId, UpdateShippingRequest req) {
        Shipping shipping = shippingRepository.findById(shippingId).orElseThrow(() -> new ResourceNotFoundException("Shipping not found"));

        if (req.status().ordinal() < shipping.getStatus().ordinal()) {
            throw new BadRequestException("Invalid status transition");
        }

        shipping.setStatus(req.status());

        if (req.status() == ShippingStatus.SHIPPED && shipping.getShippedAt() == null) {
            shipping.setShippedAt(LocalDateTime.now());
            syncOrderStatus(shipping.getOrder(), OrderStatus.SHIPPED);
        }

        if (req.status() == ShippingStatus.DELIVERED && shipping.getDeliveredAt() == null) {
            shipping.setDeliveredAt(LocalDateTime.now());
            syncOrderStatus(shipping.getOrder(), OrderStatus.DELIVERED);
        }

        return entityMapperService.toShippingResponse(shippingRepository.save(shipping));
    }

    private void syncOrderStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        orderRepository.save(order);
    }
}