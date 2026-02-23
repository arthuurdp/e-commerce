package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.address.AddressResponse;
import com.arthuurdp.e_commerce.entities.dtos.address.CreateAddressRequest;
import com.arthuurdp.e_commerce.entities.dtos.address.UpdateAddressRequest;
import com.arthuurdp.e_commerce.services.AddressService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/addresses")
public class AddressController {
    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @GetMapping
    public Page<AddressResponse> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.findAll(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> create(@RequestBody CreateAddressRequest req) {
        AddressResponse response = service.create(req);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AddressResponse> update(@PathVariable Long id, @RequestBody UpdateAddressRequest req) {
        return ResponseEntity.ok().body(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
