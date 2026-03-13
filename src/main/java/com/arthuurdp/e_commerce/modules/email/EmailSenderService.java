package com.arthuurdp.e_commerce.modules.email;

import com.arthuurdp.e_commerce.modules.shipping.entity.Shipping;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    private final JavaMailSender sender;

    public EmailSenderService(JavaMailSender sender) {
        this.sender = sender;
    }

    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your verification code");
        message.setText("""
                Hi! Here is your email verification code:

                ── VERIFICATION CODE ───────────────────────
                %s
                ────────────────────────────────────────────

                This code expires in 15 minutes.
                If you didn't request this, you can safely ignore this email.
                """.formatted(code));
        sender.send(message);
    }

    public void sendPasswordVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your password change code");
        message.setText("""
                You requested a password change. Here is your confirmation code:

                ── CONFIRMATION CODE ───────────────────────
                %s
                ────────────────────────────────────────────

                This code expires in 15 minutes.
                If you didn't request this, please secure your account immediately.
                """.formatted(code));
        sender.send(message);
    }

    public void sendWelcome(String to, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome! Your account is ready 🎉");
        message.setText("""
                Hi %s, welcome!

                Your email has been verified and your account is ready to use.
                You can now browse our store, add products to your cart and place orders.

                If you have any questions, reply to this email.
                """.formatted(firstName));
        sender.send(message);
    }

    public void sendEmailChanged(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your email has been updated");
        message.setText("""
                This is a confirmation that your account email has been changed successfully.

                Your new login email is: %s

                If you did not make this change, please contact us immediately by replying to this email.
                """.formatted(to));
        sender.send(message);
    }

    public void sendPasswordChanged(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your password has been updated");
        message.setText("""
                This is a confirmation that your account password has been changed successfully.

                If you did not make this change, please contact us immediately by replying to this email.
                """);
        sender.send(message);
    }

    public void sendPasswordResetCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset your password");
        message.setText("""
                You requested a password reset. Here is your code:

                ── RESET CODE ──────────────────────────────
                %s
                ────────────────────────────────────────────

                This code expires in 15 minutes.
                If you didn't request a password reset, you can safely ignore this email.
                """.formatted(code));
        sender.send(message);
    }

    public void sendOrderConfirmation(String to, Shipping shipping) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your order has been shipped! 📦 Order #" + shipping.getOrder().getId());
        message.setText("""
                Good news! Your order #%d has been shipped.

                ── SHIPPING DETAILS ────────────────────────
                Carrier:        %s
                Tracking code:  %s
                Shipping cost:  R$ %s

                ── TRACK YOUR PACKAGE ──────────────────────
                %s

                ── DELIVERY ADDRESS ────────────────────────
                %s, %s - %s
                %s / %s - %s

                If you have any questions, reply to this email.
                """.formatted(
                shipping.getOrder().getId(),
                shipping.getCarrier()      != null ? shipping.getCarrier()      : "N/A",
                shipping.getTrackingCode() != null ? shipping.getTrackingCode() : "N/A",
                shipping.getShippingCost() != null ? shipping.getShippingCost() : "N/A",
                shipping.getTrackingUrl()  != null ? shipping.getTrackingUrl()  : "N/A",
                shipping.getOrder().getAddress().getStreet(),
                shipping.getOrder().getAddress().getNumber(),
                shipping.getOrder().getAddress().getNeighborhood(),
                shipping.getOrder().getAddress().getCity().getName(),
                shipping.getOrder().getAddress().getCity().getState().getUf(),
                shipping.getOrder().getAddress().getPostalCode()
        ));
        sender.send(message);
    }
}