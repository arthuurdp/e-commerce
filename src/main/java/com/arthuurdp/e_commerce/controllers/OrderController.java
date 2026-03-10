package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.domain.dtos.order.OrderDetailsResponse;
import com.arthuurdp.e_commerce.domain.dtos.order.OrderResponse;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.infrastructure.security.annotations.AdminOrSelf;
import com.arthuurdp.e_commerce.services.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    @AdminOrSelf
    public ResponseEntity<Page<OrderResponse>> findByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(service.findByUser(page, size, user));
    }

    @GetMapping("/{id}")
    @AdminOrSelf
    public ResponseEntity<OrderDetailsResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }
}
