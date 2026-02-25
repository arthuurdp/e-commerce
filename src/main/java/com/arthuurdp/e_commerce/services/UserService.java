package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.EmailVerificationToken;
import com.arthuurdp.e_commerce.entities.PasswordVerificationToken;
import com.arthuurdp.e_commerce.entities.User;
import com.arthuurdp.e_commerce.entities.dtos.user.UpdateUserRequest;
import com.arthuurdp.e_commerce.entities.dtos.user.UserResponse;
import com.arthuurdp.e_commerce.exceptions.AccessDeniedException;
import com.arthuurdp.e_commerce.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.exceptions.ConflictException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.EmailVerificationTokenRepository;
import com.arthuurdp.e_commerce.repositories.PasswordVerificationTokenRepository;
import com.arthuurdp.e_commerce.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final EmailVerificationTokenRepository emailTokenRepo;
    private final PasswordVerificationTokenRepository passwordTokenRepo;
    private final EmailService emailService;
    private final EntityMapperService entityMapperService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public UserService(UserRepository userRepo, EmailVerificationTokenRepository emailTokenRepo, PasswordVerificationTokenRepository passwordTokenRepo, EmailService emailService, EntityMapperService entityMapperService, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepo = userRepo;
        this.emailTokenRepo = emailTokenRepo;
        this.passwordTokenRepo = passwordTokenRepo;
        this.emailService = emailService;
        this.entityMapperService = entityMapperService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    public UserResponse findById(Long id) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return entityMapperService.toUserResponse(user);
    }

    public Page<UserResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepo.findAll(pageable).map(entityMapperService::toUserResponse);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest req) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean updated = false;

        if (req.firstName() != null) {
            user.setFirstName(req.firstName());
            updated = true;
        }

        if (req.lastName() != null) {
            user.setLastName(req.lastName());
            updated = true;
        }

        if (req.email() != null) {
            if (req.email().isBlank()) {
                throw new BadRequestException("Email cannot be blank");
            }
            if (!req.email().equals(user.getEmail())) {
                if (userRepo.existsByEmail(req.email())) {
                    throw new ConflictException("Email already in use");
                }
                user.setEmail(req.email());
                updated = true;
            }
        }

        if (req.cpf() != null) {
            if (userRepo.existsByCpf(req.cpf()) && req.cpf().equals(user.getCpf())) {
                throw new ConflictException("CPF already in use");
            }
            user.setCpf(req.cpf());
            updated = true;
        }

        if (req.phone() != null) {
            user.setPhone(req.phone());
            updated = true;
        }

        if (req.birthDate() != null) {
            user.setBirthDate(req.birthDate());
            updated = true;
        }

        if (req.gender() != null) {
            user.setGender(req.gender());
            updated = true;
        }

        if (!updated) {
            throw new BadRequestException("No valid fields provided");
        }

        return entityMapperService.toUserResponse(userRepo.save(user));
    }

    public void delete(Long id) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepo.delete(user);
    }

    @Transactional
    public void sendEmailVerification() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        if (user.isEmailVerified()) {
            throw new BadRequestException("Email already verified");
        }

        emailTokenRepo.deleteByUserId(user.getId());

        String code = String.format("%06d", new Random().nextInt(999999));
        EmailVerificationToken token = new EmailVerificationToken(code, user, user.getEmail());
        emailTokenRepo.save(token);

        emailService.sendVerificationCode(user.getEmail(), code);
    }

    @Transactional
    public void verifyEmail(String code) {
        EmailVerificationToken token = emailTokenRepo.findByCodeAndUsedFalse(code).orElseThrow(() -> new ResourceNotFoundException("Invalid or already used code"));

        if (token.isExpired()) {
            throw new BadRequestException("Code has expired");
        }

        User user = token.getUser();

        user.setEmailVerified(true);
        token.setUsed(true);

        userRepo.save(user);
        emailTokenRepo.save(token);
        emailService.sendWelcome(user.getEmail(), user.getFirstName());
    }

    @Transactional
    public void requestEmailChange(String newEmail) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        if (newEmail.equals(user.getEmail())) {
            throw new BadRequestException("New email is the same as current");
        }
        if (userRepo.existsByEmail(newEmail)) {
            throw new ConflictException("Email already in use");
        }

        emailTokenRepo.deleteByUserId(user.getId());

        String code = String.format("%06d", new Random().nextInt(999999));
        EmailVerificationToken token = new EmailVerificationToken(code, user, newEmail);
        emailTokenRepo.save(token);

        emailService.sendVerificationCode(newEmail, code);
    }

    @Transactional
    public void confirmEmailChange(String code) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        EmailVerificationToken token = emailTokenRepo.findByCodeAndUsedFalse(code).orElseThrow(() -> new ResourceNotFoundException("Invalid or already used code"));

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

        userRepo.save(user);
        emailTokenRepo.save(token);
        emailService.sendEmailChanged(user.getEmail());
    }

    @Transactional
    public void requestPasswordChange(String newPassword) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BadRequestException("Password can't be the same as current");
        }
        if (!user.isEmailVerified()) {
            throw new AccessDeniedException("Email not verified");
        }

        passwordTokenRepo.deleteByUserId(user.getId());

        String code = String.format("%06d", new Random().nextInt(999999));
        PasswordVerificationToken token = new PasswordVerificationToken(code, user, newPassword);

        passwordTokenRepo.save(token);

        emailService.sendPasswordVerificationCode(user.getEmail(), code);
    }

    @Transactional
    public void confirmPasswordChange(String code) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        PasswordVerificationToken token = passwordTokenRepo.findByCodeAndUsedFalse(code).orElseThrow(() -> new ResourceNotFoundException("Invalid or already used code"));

        if (!token.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Invalid code");
        }
        if (token.isExpired()) {
            throw new BadRequestException("Code has expired");
        }

        user.setPassword(passwordEncoder.encode(token.getPendingPassword()));
        user.setPasswordChangeVerified(true);
        token.setUsed(true);

        userRepo.save(user);
        passwordTokenRepo.save(token);
        emailService.sendPasswordChanged(user.getEmail());
    }

}
