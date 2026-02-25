package com.arthuurdp.e_commerce.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private JavaMailSender sender;

    public EmailService(JavaMailSender sender) {
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
    }

    public void sendPasswordChanged(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password changed successfully");
        message.setText("Your password has been changed successfully.");
        sender.send(message);
    }
}
