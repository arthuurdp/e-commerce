package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.carrier.CarrierResponse;
import com.arthuurdp.e_commerce.entities.dtos.carrier.CreateCarrierRequest;
import com.arthuurdp.e_commerce.entities.dtos.carrier.UpdateCarrierRequest;
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
    private final CarrierService carrierService;

    public CarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @GetMapping
    public ResponseEntity<Page<CarrierResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(carrierService.findAll(page, size));
    }

    @GetMapping("/states/{stateId}")
    public ResponseEntity<Page<CarrierResponse>> findAllByStateId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long stateId
    ) {
        return ResponseEntity.ok().body(carrierService.findAllByStateId(page, size, stateId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarrierResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(carrierService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CarrierResponse> create(@RequestBody @Valid CreateCarrierRequest req) {
        CarrierResponse response = carrierService.create(req);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CarrierResponse> update(@PathVariable Long id, @RequestBody @Valid UpdateCarrierRequest req) {
        return ResponseEntity.ok().body(carrierService.update(id, req));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carrierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
