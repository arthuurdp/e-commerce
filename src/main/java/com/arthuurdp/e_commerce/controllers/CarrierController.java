package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.Carrier;
import com.arthuurdp.e_commerce.services.CarrierService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carriers")
public class CarrierController {
    private final CarrierService carrierService;

    public CarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @GetMapping
    public ResponseEntity<Page<Carrier>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(carrierService.findAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrier> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(carrierService.findById(id));
    }
}
