package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.OrderRequest;
import com.arthuurdp.e_commerce.entities.dtos.OrderResponse;
import com.arthuurdp.e_commerce.services.OrderService;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest req, @AuthenticationPrincipal UserDetails userDetails) {
        OrderResponse response = orderService.create(req, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.findById(id, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> findByUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.findByUser(userDetails.getUsername()));
    }
}
