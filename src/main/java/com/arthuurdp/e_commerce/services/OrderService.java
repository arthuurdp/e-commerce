package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.entities.*;
import com.arthuurdp.e_commerce.domain.dtos.order.OrderDetailsResponse;
import com.arthuurdp.e_commerce.domain.dtos.order.OrderResponse;
import com.arthuurdp.e_commerce.domain.enums.OrderStatus;
import com.arthuurdp.e_commerce.exceptions.AccessDeniedException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final EntityMapperService entityMapperService;
    private final AuthService authService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, EntityMapperService entityMapperService, AuthService authService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.entityMapperService = entityMapperService;
        this.authService = authService;
        this.productService = productService;
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
    public Page<OrderResponse> findByUser(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        User user = authService.getCurrentUser();
        return orderRepository.findByUserId(pageable, user.getId()).map(entityMapperService::toOrderResponse);
    }

    @Transactional
    public Order createOrder(User user, Address address, Cart cart) {
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(cart.total());

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            productService.decreaseStock(product, cartItem.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            order.getItems().add(orderItem);
        }
        return orderRepository.save(order);
    }
}
