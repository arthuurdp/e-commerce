package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.Carrier;
import com.arthuurdp.e_commerce.entities.dtos.carrier.CarrierResponse;
import com.arthuurdp.e_commerce.services.CarrierService;
import com.arthuurdp.e_commerce.services.EntityMapperService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carriers")
public class CarrierController {
    private final CarrierService carrierService;
    private final EntityMapperService entityMapperService;

    public CarrierController(CarrierService carrierService, EntityMapperService entityMapperService) {
        this.carrierService = carrierService;
        this.entityMapperService = entityMapperService;
    }

    @GetMapping
    public ResponseEntity<Page<CarrierResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(entityMapperService.);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarrierResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(entityMapperService.toCarrierResponse(carrierService.findById(id)));
    }
}
