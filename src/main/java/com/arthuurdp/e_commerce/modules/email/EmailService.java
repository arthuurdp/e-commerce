package com.arthuurdp.e_commerce.modules.email;

import com.arthuurdp.e_commerce.modules.email.entity.EmailVerificationToken;
import com.arthuurdp.e_commerce.modules.email.entity.PasswordResetToken;
import com.arthuurdp.e_commerce.modules.email.entity.PasswordVerificationToken;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.shared.exceptions.AccessDeniedException;
import com.arthuurdp.e_commerce.shared.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.shared.exceptions.ConflictException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.modules.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class EmailService {
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordVerificationTokenRepository passwordVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public EmailService(EmailVerificationTokenRepository emailVerificationTokenRepository, PasswordVerificationTokenRepository passwordVerificationTokenRepository, PasswordResetTokenRepository passwordResetTokenRepository, UserRepository userRepository, EmailSenderService emailSenderService, PasswordEncoder passwordEncoder) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordVerificationTokenRepository = passwordVerificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
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
        if (newEmail.equals(user.getEmail()) || userRepository.existsByEmail(newEmail)) {
            throw new BadRequestException("E-mail already taken");
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
            throw new BadRequestException("Please set a different password");
        }

        passwordVerificationTokenRepository.deleteByUserId(user.getId());

        String code = String.format("%06d", SECURE_RANDOM.nextInt(999999));
        String encoded = passwordEncoder.encode(newPassword);
        PasswordVerificationToken token = new PasswordVerificationToken(code, user, encoded);

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

        user.setPassword(token.getPendingPassword());
        user.setPasswordChangeVerified(true);
        token.setUsed(true);

        userRepository.save(user);
        passwordVerificationTokenRepository.save(token);
        emailSenderService.sendPasswordChanged(user.getEmail());
    }

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email.toLowerCase()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        passwordResetTokenRepository.deleteByUserId(user.getId());

        String code = String.format("%06d", SECURE_RANDOM.nextInt(999999));
        PasswordResetToken token = new PasswordResetToken(code, user);
        passwordResetTokenRepository.save(token);

        emailSenderService.sendPasswordResetCode(user.getEmail(), code);
    }

    @Transactional
    public void confirmPasswordReset(String code, String newPassword) {
        PasswordResetToken token = passwordResetTokenRepository.findByCodeAndUsedFalse(code).orElseThrow(() -> new ResourceNotFoundException("Invalid or already used code"));

        if (token.isExpired()) {
            throw new BadRequestException("Code has expired");
        }

        User user = token.getUser();

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BadRequestException("New password must be different from your current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        token.setUsed(true);

        userRepository.save(user);
        passwordResetTokenRepository.save(token);
        emailSenderService.sendPasswordChanged(user.getEmail());
    }
}
