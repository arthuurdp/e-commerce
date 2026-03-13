package com.arthuurdp.e_commerce.modules.email;

import com.arthuurdp.e_commerce.modules.email.dtos.*;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
public class EmailController {
    private final EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }

    @PostMapping("/verify-email/send")
    public ResponseEntity<Map<String, String>> sendVerification(
            @AuthenticationPrincipal User user
    ) {
        service.sendEmailVerification(user);
        return ResponseEntity.ok(Map.of("message", "Verification email sent successfully!"));
    }

    @PostMapping("/verify-email/confirm")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @RequestBody @Valid VerifyCodeRequest req
    ) {
        service.verifyEmail(req.code());
        return ResponseEntity.ok(Map.of("message", "Email verified successfully!"));
    }

    @PostMapping("/email/change")
    public ResponseEntity<Map<String, String>> requestEmailChange(
            @RequestBody @Valid ChangeEmailRequest req,
            @AuthenticationPrincipal User user
    ) {
        service.requestEmailChange(req.email(), user);
        return ResponseEntity.ok(Map.of("message", "A confirmation code has been sent to your new email!"));
    }

    @PostMapping("/email/confirm")
    public ResponseEntity<Map<String, String>> confirmEmailChange(
            @RequestBody @Valid VerifyCodeRequest req,
            @AuthenticationPrincipal User user
    ) {
        service.confirmEmailChange(req.code(), user);
        return ResponseEntity.ok(Map.of("message", "Email changed successfully!"));
    }

    @PostMapping("/password/change")
    public ResponseEntity<Map<String, String>> requestPasswordChange(
            @RequestBody @Valid ChangePasswordRequest req,
            @AuthenticationPrincipal User user
    ) {
        service.requestPasswordChange(req.password(), user);
        return ResponseEntity.ok(Map.of("message", "A confirmation code has been sent to your email!"));
    }

    @PostMapping("/password/confirm")
    public ResponseEntity<Map<String, String>> confirmPasswordChange(
            @RequestBody @Valid VerifyCodeRequest req,
            @AuthenticationPrincipal User user
    ) {
        service.confirmPasswordChange(req.code(), user);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully!"));
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest req
    ) {
        service.requestPasswordReset(req.email());
        return ResponseEntity.ok(Map.of("message", "A reset code has been sent."));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody @Valid ResetPasswordRequest req
    ) {
        service.confirmPasswordReset(req.code(), req.newPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully!"));
    }
}