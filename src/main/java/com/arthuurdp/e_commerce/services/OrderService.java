package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.*;
import com.arthuurdp.e_commerce.entities.dtos.OrderItemRequest;
import com.arthuurdp.e_commerce.entities.dtos.OrderItemResponse;
import com.arthuurdp.e_commerce.entities.dtos.OrderRequest;
import com.arthuurdp.e_commerce.entities.dtos.OrderResponse;
import com.arthuurdp.e_commerce.entities.enums.OrderStatus;
import com.arthuurdp.e_commerce.entities.enums.PaymentStatus;
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
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final EntityMapperService entityMapperService;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, AddressRepository addressRepository, ProductRepository productRepository, PaymentRepository paymentRepository, EntityMapperService entityMapperService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
        this.entityMapperService = entityMapperService;
    }

    public OrderResponse create(OrderRequest req, String userEmail) {
        User user = (User) userRepository.findByEmail(userEmail);
        Address address = addressRepository.findById(req.addressId()).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);

        // Monta os itens e calcula o total
        BigDecimal total = BigDecimal.ZERO;
        Set<OrderItem> orderItems = new HashSet<>();

        for (OrderItemRequest itemDTO : req.items()) {
            Product product = productRepository.findById(itemDTO.productId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemDTO.quantity());
            item.setUnitPrice(product.getPrice());
            item.setOrder(order);

            orderItems.add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.quantity())));
        }

        order.setTotal(total);
        order.getItems().addAll(orderItems);

        Payment payment = new Payment();
        payment.setAmount(total);

        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);
        paymentRepository.save(payment);
        return entityMapperService.toOrderResponse(savedOrder);
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
