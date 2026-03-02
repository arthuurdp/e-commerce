package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.*;
import com.arthuurdp.e_commerce.entities.dtos.order.OrderDetailsResponse;
import com.arthuurdp.e_commerce.entities.dtos.order.OrderResponse;
import com.arthuurdp.e_commerce.entities.enums.OrderStatus;
import com.arthuurdp.e_commerce.exceptions.AccessDeniedException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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
    public List<OrderResponse> findByUser() {
        User user = authService.getCurrentUser();
        return orderRepository.findByUserId(user.getId()).stream().map(entityMapperService::toOrderResponse).toList();
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
