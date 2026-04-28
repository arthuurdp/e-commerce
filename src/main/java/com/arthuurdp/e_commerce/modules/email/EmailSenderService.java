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
        message.setSubject("Verification code");
        message.setText("""
                Your code: %s

                Expires in 15 minutes.
                """.formatted(code));
        sender.send(message);
    }

    public void sendPasswordVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password change code");
        message.setText("""
                Your code: %s

                Expires in 15 minutes.
                """.formatted(code));
        sender.send(message);
    }

    public void sendWelcome(String to, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome!");
        message.setText("""
                Hi %s,

                Your account is ready.
                """.formatted(firstName));
        sender.send(message);
    }

    public void sendEmailChanged(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email updated");
        message.setText("""
                Your email was updated to: %s
                """.formatted(to));
        sender.send(message);
    }

    public void sendPasswordChanged(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password updated");
        message.setText("""
                Your password was changed.
                """);
        sender.send(message);
    }

    public void sendPasswordResetCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset code");
        message.setText("""
                Your code: %s

                Expires in 15 minutes.
                """.formatted(code));
        sender.send(message);
    }

    public void sendOrderConfirmation(String to, Shipping shipping) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Order shipped #" + shipping.getOrder().getId());
        message.setText("""
                Order #%d shipped.

                Carrier: %s
                Tracking: %s

                Address:
                %s, %s - %s
                %s/%s - %s
                """.formatted(
                shipping.getOrder().getId(),
                shipping.getCarrier()      != null ? shipping.getCarrier()      : "N/A",
                shipping.getTrackingCode() != null ? shipping.getTrackingCode() : "N/A",
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