package com.arthuurdp.e_commerce.modules.address;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.modules.address.dtos.AddressResponse;
import com.arthuurdp.e_commerce.modules.address.dtos.CreateAddressRequest;
import com.arthuurdp.e_commerce.modules.address.dtos.UpdateAddressRequest;
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
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return service.findAll(page, size, authenticatedUser.getUser());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok().body(service.findById(id, authenticatedUser.getUser()));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressResponse> create(
            @RequestBody @Valid CreateAddressRequest req,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        AddressResponse response = service.create(req, authenticatedUser.getUser());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateAddressRequest req,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok().body(service.update(id, req, authenticatedUser.getUser()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        service.delete(id, authenticatedUser.getUser());
        return ResponseEntity.noContent().build();
    }
}
