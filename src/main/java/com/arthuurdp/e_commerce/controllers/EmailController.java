package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.email.ChangeEmailRequest;
import com.arthuurdp.e_commerce.entities.dtos.email.ConfirmEmailChangeRequest;
import com.arthuurdp.e_commerce.entities.dtos.email.VerifyEmailRequest;
import com.arthuurdp.e_commerce.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class EmailController {
    private final UserService userService;

    public EmailController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/verify-email/send")
    public ResponseEntity<Void> sendVerification() {
        userService.sendEmailVerification();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-email/confirm")
    public ResponseEntity<Void> verifyEmail(@RequestBody @Valid VerifyEmailRequest req) {
        userService.verifyEmail(req.code());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email/change")
    public ResponseEntity<Void> requestEmailChange(@RequestBody @Valid ChangeEmailRequest req) {
        userService.requestEmailChange(req.newEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email/confirm")
    public ResponseEntity<Void> confirmEmailChange(@RequestBody @Valid ConfirmEmailChangeRequest req) {
        userService.confirmEmailChange(req.code());
        return ResponseEntity.noContent().build();
    }

}
