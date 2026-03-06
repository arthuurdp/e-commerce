package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.entities.Order;
import com.arthuurdp.e_commerce.domain.entities.Shipping;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.domain.dtos.shipping.CreateShippingRequest;
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
    private final AuthService authService;
    private final EntityMapperService entityMapperService;

    public ShippingService(ShippingRepository shippingRepository, OrderRepository orderRepository, AuthService authService, EntityMapperService entityMapperService) {
        this.shippingRepository = shippingRepository;
        this.orderRepository = orderRepository;
        this.authService = authService;
        this.entityMapperService = entityMapperService;
    }

    @Transactional
    public ShippingResponse create(CreateShippingRequest req) {
        if (shippingRepository.existsByOrderId(req.orderId())) {
            throw new ConflictException("Shipping already exists");
        }
        return entityMapperService.toShippingResponse(shippingRepository.save(new Shipping(order)));
    }

    public ShippingResponse findByOrderId(Long orderId) {
        User user = authService.getCurrentUser();
        Shipping shipping = shippingRepository.findByOrderIdAndOrderUserId(orderId, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Shipping not found"));
        return entityMapperService.toShippingResponse(shipping);
    }

    @Transactional
    public ShippingResponse update(Long shippingId, UpdateShippingRequest req) {
        Shipping shipping = shippingRepository.findById(shippingId).orElseThrow(() -> new ResourceNotFoundException("Shipping not found"));

        if (req.status().ordinal() < shipping.getStatus().ordinal()) {
            throw new BadRequestException("Invalid status transition");
        }
        if (req.carrier() != null) {
            shipping.s(req.carrier());
        }
        if (req.trackingCode() != null) {
            shipping.setTrackingCode(req.trackingCode());
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