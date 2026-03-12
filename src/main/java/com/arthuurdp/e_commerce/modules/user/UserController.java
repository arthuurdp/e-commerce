package com.arthuurdp.e_commerce.modules.user;

import com.arthuurdp.e_commerce.modules.user.dtos.UpdateUserRequest;
import com.arthuurdp.e_commerce.modules.user.dtos.UserResponse;
import com.arthuurdp.e_commerce.infrastructure.security.annotations.AdminOrSelf;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(service.findAll(page, size));
    }
    
    @GetMapping("/{id}")
    @AdminOrSelf
    public ResponseEntity<UserResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PatchMapping("/{id}")
    @AdminOrSelf
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequest req
    ) {
        return ResponseEntity.ok().body(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @AdminOrSelf
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
