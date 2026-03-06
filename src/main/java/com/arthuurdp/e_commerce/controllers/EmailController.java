package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.domain.dtos.email.ChangeEmailRequest;
import com.arthuurdp.e_commerce.domain.dtos.email.ChangePasswordRequest;
import com.arthuurdp.e_commerce.domain.dtos.email.VerifyCodeRequest;
import com.arthuurdp.e_commerce.services.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/verify-email/send")
    public ResponseEntity<Void> sendVerification() {
        emailService.sendEmailVerification();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-email/confirm")
    public ResponseEntity<Void> verifyEmail(@RequestBody @Valid VerifyCodeRequest req) {
        emailService.verifyEmail(req.code());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email/change")
    public ResponseEntity<Void> requestEmailChange(@RequestBody @Valid ChangeEmailRequest req) {
        emailService.requestEmailChange(req.newEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email/confirm")
    public ResponseEntity<Void> confirmEmailChange(@RequestBody @Valid VerifyCodeRequest req) {
        emailService.confirmEmailChange(req.code());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/change")
    public ResponseEntity<Void> requestPasswordChange(@RequestBody @Valid ChangePasswordRequest req) {
        emailService.requestPasswordChange(req.newPassword());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/confirm")
    public ResponseEntity<Void> confirmPasswordChange(@RequestBody @Valid VerifyCodeRequest req) {
        emailService.confirmPasswordChange(req.code());
        return ResponseEntity.noContent().build();
    }
}
