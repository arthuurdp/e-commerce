package com.arthuurdp.e_commerce.modules.user;

import com.arthuurdp.e_commerce.modules.user.dtos.UpdateUserRequest;
import com.arthuurdp.e_commerce.modules.user.dtos.UserResponse;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok().body(service.findById(id, user));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequest req,
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

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> findCurrentUser(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok().body(service.findCurrentUser(user));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @RequestBody @Valid UpdateUserRequest req,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok().body(service.updateCurrentUser(req, user));
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteCurrentUser(
            @AuthenticationPrincipal User user
    ) {
        service.deleteCurrentUser(user);
        return ResponseEntity.noContent().build();
    }
}
