package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.domain.dtos.carrier.CarrierResponse;
import com.arthuurdp.e_commerce.domain.dtos.carrier.CreateCarrierRequest;
import com.arthuurdp.e_commerce.domain.dtos.carrier.UpdateCarrierRequest;
import com.arthuurdp.e_commerce.domain.enums.CarrierStatus;
import com.arthuurdp.e_commerce.domain.enums.Region;
import com.arthuurdp.e_commerce.services.CarrierService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/carriers")
@PreAuthorize("hasRole('ADMIN')")
public class CarrierController {
    private final CarrierService service;

    public CarrierController(CarrierService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<CarrierResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(service.findAll(page, size));
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<Page<CarrierResponse>> findAllByRegion(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Region region,
            @RequestParam(required = false) CarrierStatus status
    ) {
        return ResponseEntity.ok().body(service.findAllByRegion(page, size, region, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarrierResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<CarrierResponse> create(
            @RequestBody @Valid CreateCarrierRequest req
    ) {
        CarrierResponse response = service.create(req);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CarrierResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCarrierRequest req
    ) {
        return ResponseEntity.ok().body(service.update(id, req));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
