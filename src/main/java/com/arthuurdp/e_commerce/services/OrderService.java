package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.entities.*;
import com.arthuurdp.e_commerce.domain.dtos.order.OrderDetailsResponse;
import com.arthuurdp.e_commerce.domain.dtos.order.OrderResponse;
import com.arthuurdp.e_commerce.domain.enums.OrderStatus;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.OrderRepository;
import com.arthuurdp.e_commerce.services.mappers.OrderMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository repo;
    private final OrderMapper mapper;
    private final ProductService productService;

    public OrderService(OrderRepository repo, OrderMapper mapper, ProductService productService) {
        this.repo = repo;
        this.mapper = mapper;
        this.productService = productService;
    }

    @Transactional
    public OrderDetailsResponse findById(Long id) {
        return mapper.toOrderDetailsResponse(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found")));
    }

    @Transactional
    public Page<OrderResponse> findByUser(int page, int size, User user) {
        return repo.findByUserId(PageRequest.of(page, size), user.getId()).map(mapper::toOrderResponse);
    }

    @Transactional
    public Order createOrder(User user, Address address, Cart cart) {
        Order order = new Order(
                user,
                address,
                OrderStatus.PENDING,
                cart.total(),
                address.getCity().getState()
        );

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            productService.decreaseStock(product, cartItem.getQuantity());

            OrderItem orderItem = new OrderItem(
                    order,
                    product,
                    cartItem.getQuantity(),
                    product.getPrice()
            );

            order.getItems().add(orderItem);
        }
        return repo.save(order);
    }
}
