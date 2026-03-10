package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.domain.dtos.email.ChangeEmailRequest;
import com.arthuurdp.e_commerce.domain.dtos.email.ChangePasswordRequest;
import com.arthuurdp.e_commerce.domain.dtos.email.VerifyCodeRequest;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.services.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class EmailController {
    private final EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }

    @PostMapping("/verify-email/send")
    public ResponseEntity<Void> sendVerification(
            @AuthenticationPrincipal User user
    ) {
        service.sendEmailVerification(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-email/confirm")
    public ResponseEntity<Void> verifyEmail(
            @RequestBody @Valid VerifyCodeRequest req
    ) {
        service.verifyEmail(req.code());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email/change")
    public ResponseEntity<Void> requestEmailChange(
            @RequestBody @Valid ChangeEmailRequest req,
            @AuthenticationPrincipal User user
    ) {
        service.requestEmailChange(req.email(), user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email/confirm")
    public ResponseEntity<Void> confirmEmailChange(
            @RequestBody @Valid VerifyCodeRequest req,
            @AuthenticationPrincipal User user
    ) {
        service.confirmEmailChange(req.code(), user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/change")
    public ResponseEntity<Void> requestPasswordChange(
            @RequestBody @Valid ChangePasswordRequest req,
            @AuthenticationPrincipal User user
            ) {
        service.requestPasswordChange(req.password(), user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/confirm")
    public ResponseEntity<Void> confirmPasswordChange(
            @RequestBody @Valid VerifyCodeRequest req,
            @AuthenticationPrincipal User user
    ) {
        service.confirmPasswordChange(req.code(), user);
        return ResponseEntity.noContent().build();
    }
}
