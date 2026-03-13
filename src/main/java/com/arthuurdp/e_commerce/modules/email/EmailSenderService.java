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
        message.setSubject("Email verification code");
        message.setText("Your verification code is: " + code + "\n\nThis code expires in 15 minutes.");
        sender.send(message);
    }

    public void sendPasswordVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password verification code");
        message.setText("Your password verification code is: " + code + "\n\nThis code expires in 15 minutes.");
        sender.send(message);
    }

    public void sendWelcome(String to, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to our store!");
        message.setText("Hello " + firstName + ", welcome! Your account has been created successfully.");
        sender.send(message);
    }

    public void sendEmailChanged(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email changed successfully");
        message.setText("Your email has been changed successfully.");
        sender.send(message);
    }

    public void sendPasswordChanged(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password changed successfully");
        message.setText("Your password has been changed successfully.");
        sender.send(message);
    }

    public void sendPasswordResetCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password reset code");
        message.setText("Your password reset code is: " + code);
        sender.send(message);
    }

    public void sendOrderConfirmation(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Order confirmation");
        message.setText("Your order has been placed successfully!");
        sender.send(message);
    }

    public void sendOrderCancelled(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Order cancelled");
        message.setText("Your order has been cancelled.");
    }

    public void sendShippingConfirmation(String to, Shipping shipping) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your order has been shipped! 📦 Order #" + shipping.getOrder().getId());

        String text = """
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
        );

        message.setText(text);
        sender.send(message);
    }
}
