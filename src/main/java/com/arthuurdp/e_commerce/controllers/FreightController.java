package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.domain.dtos.shipping.FreightResponse;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.services.FreightService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/freights")
public class FreightController {
    private final FreightService service;

    public FreightController(FreightService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<FreightResponse>> findAll(
            @RequestParam String postalCode,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok().body(service.calculate(postalCode, user));
    }
}
