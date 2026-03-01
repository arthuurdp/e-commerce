package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.order_item.OrderResponse;
import com.arthuurdp.e_commerce.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> findByUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.findByUser(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.findById(id, userDetails.getUsername()));
    }
}
