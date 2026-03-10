package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.entities.EmailVerificationToken;
import com.arthuurdp.e_commerce.domain.entities.PasswordVerificationToken;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.exceptions.AccessDeniedException;
import com.arthuurdp.e_commerce.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.exceptions.ConflictException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.EmailVerificationTokenRepository;
import com.arthuurdp.e_commerce.repositories.PasswordVerificationTokenRepository;
import com.arthuurdp.e_commerce.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class EmailService {
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordVerificationTokenRepository passwordVerificationTokenRepository;
    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public EmailService(EmailVerificationTokenRepository emailVerificationTokenRepository, PasswordVerificationTokenRepository passwordVerificationTokenRepository, UserRepository userRepository, EmailSenderService emailSenderService, PasswordEncoder passwordEncoder) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordVerificationTokenRepository = passwordVerificationTokenRepository;
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void verifyEmail(String code) {
        EmailVerificationToken token = emailVerificationTokenRepository.findByCodeAndUsedFalse(code).orElseThrow(() -> new ResourceNotFoundException("Invalid or already used code"));

        if (token.isExpired()) {
            throw new BadRequestException("Code has expired");
        }

        User user = token.getUser();

        user.setEmailVerified(true);
        token.setUsed(true);

        userRepository.save(user);
        emailVerificationTokenRepository.save(token);
        emailSenderService.sendWelcome(user.getEmail(), user.getFirstName());
    }

    @Transactional
    public void sendEmailVerification(User user) {
        if (user.isEmailVerified()) {
            throw new BadRequestException("Email already verified");
        }

        emailVerificationTokenRepository.deleteByUserId(user.getId());

        String code = String.format("%06d", SECURE_RANDOM.nextInt(999999));
        EmailVerificationToken token = new EmailVerificationToken(code, user, user.getEmail());
        emailVerificationTokenRepository.save(token);

        emailSenderService.sendVerificationCode(user.getEmail(), code);
    }

    @Transactional
    public void requestEmailChange(String newEmail, User user) {
        if (newEmail.equals(user.getEmail())) {
            throw new BadRequestException("New email is the same as current");
        }
        if (userRepository.existsByEmail(newEmail)) {
            throw new ConflictException("Email already in use");
        }

        emailVerificationTokenRepository.deleteByUserId(user.getId());

        String code = String.format("%06d", SECURE_RANDOM.nextInt(999999));
        EmailVerificationToken token = new EmailVerificationToken(code, user, newEmail);
        emailVerificationTokenRepository.save(token);

        emailSenderService.sendVerificationCode(newEmail, code);
    }

    @Transactional
    public void confirmEmailChange(String code, User user) {
        EmailVerificationToken token = emailVerificationTokenRepository.findByCodeAndUsedFalse(code).orElseThrow(() -> new ResourceNotFoundException("Invalid or already used code"));

        if (!token.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Invalid code");
        }
        if (token.isExpired()) {
            throw new BadRequestException("Code has expired");
        }
        if (token.getPendingEmail().equals(user.getEmail())) {
            throw new BadRequestException("This code was for a different purpose or email is already set");
        }

        user.setEmail(token.getPendingEmail());
        user.setEmailVerified(true);
        token.setUsed(true);

        userRepository.save(user);
        emailVerificationTokenRepository.save(token);
        emailSenderService.sendEmailChanged(user.getEmail());
    }

    @Transactional
    public void requestPasswordChange(String newPassword, User user) {
        if (!user.isEmailVerified()) {
            throw new AccessDeniedException("Please verify your email");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BadRequestException("Password has to be different than the others");
        }

        passwordVerificationTokenRepository.deleteByUserId(user.getId());

        String code = String.format("%06d", SECURE_RANDOM.nextInt(999999));
        PasswordVerificationToken token = new PasswordVerificationToken(code, user, newPassword);

        passwordVerificationTokenRepository.save(token);

        emailSenderService.sendPasswordVerificationCode(user.getEmail(), code);
    }

    @Transactional
    public void confirmPasswordChange(String code, User user) {
        PasswordVerificationToken token = passwordVerificationTokenRepository.findByCodeAndUsedFalse(code).orElseThrow(() -> new ResourceNotFoundException("Invalid or already used code"));

        if (!token.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Invalid code");
        }
        if (token.isExpired()) {
            throw new BadRequestException("Code has expired");
        }

        user.setPassword(passwordEncoder.encode(token.getPendingPassword()));
        user.setPasswordChangeVerified(true);
        token.setUsed(true);

        userRepository.save(user);
        passwordVerificationTokenRepository.save(token);
        emailSenderService.sendPasswordChanged(user.getEmail());
    }
}
