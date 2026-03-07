package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.domain.dtos.address.AddressResponse;
import com.arthuurdp.e_commerce.domain.dtos.address.CreateAddressRequest;
import com.arthuurdp.e_commerce.domain.dtos.address.UpdateAddressRequest;
import com.arthuurdp.e_commerce.services.AddressService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }


    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<AddressResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user
            ) {
        return service.findAll(page, size, user.getId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
            ) {
        return ResponseEntity.ok().body(service.findById(id, user.getId()));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressResponse> create(
            @RequestBody @Valid CreateAddressRequest req,
            @AuthenticationPrincipal User user
    ) {
        AddressResponse response = service.create(req, user);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateAddressRequest req,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok().body(service.update(id, req, user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        service.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
