package com.arthuurdp.e_commerce.modules.order;

import com.arthuurdp.e_commerce.modules.address.entity.Address;
import com.arthuurdp.e_commerce.modules.cart.entity.Cart;
import com.arthuurdp.e_commerce.modules.cart.entity.CartItem;
import com.arthuurdp.e_commerce.modules.order.dtos.OrderDetailsResponse;
import com.arthuurdp.e_commerce.modules.order.dtos.OrderResponse;
import com.arthuurdp.e_commerce.modules.order.enums.OrderStatus;
import com.arthuurdp.e_commerce.shared.exceptions.AccessDeniedException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.modules.order.entity.Order;
import com.arthuurdp.e_commerce.modules.order.entity.OrderItem;
import com.arthuurdp.e_commerce.modules.order.mapper.OrderMapper;
import com.arthuurdp.e_commerce.modules.product.ProductService;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import org.springframework.transaction.annotation.Transactional;
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
    public OrderDetailsResponse findById(Long id, User user) {
        Order order = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!user.getId().equals(order.getUser().getId()) && !user.isAdmin()) {
            throw new AccessDeniedException("Access denied");
        }

        return mapper.toOrderDetailsResponse(order);
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
