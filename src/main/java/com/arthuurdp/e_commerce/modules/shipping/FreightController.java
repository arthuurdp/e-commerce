package com.arthuurdp.e_commerce.modules.shipping;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import com.arthuurdp.e_commerce.modules.shipping.dtos.FreightResponse;
import com.arthuurdp.e_commerce.modules.user.entity.User;
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
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
            ) {
        return ResponseEntity.ok().body(service.calculate(postalCode, authenticatedUser.getUser()));
    }
}
