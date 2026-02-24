package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.EmailVerificationToken;
import com.arthuurdp.e_commerce.entities.User;
import com.arthuurdp.e_commerce.entities.dtos.user.UpdateUserRequest;
import com.arthuurdp.e_commerce.entities.dtos.user.UserResponse;
import com.arthuurdp.e_commerce.exceptions.AccessDeniedException;
import com.arthuurdp.e_commerce.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.exceptions.ConflictException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.EmailVerificationTokenRepository;
import com.arthuurdp.e_commerce.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final EmailVerificationTokenRepository tokenRepo;
    private final EmailService emailService;
    private final EntityMapperService entityMapperService;
    private final AuthService authService;

    public UserService(UserRepository userRepo, EmailVerificationTokenRepository tokenRepo, EmailService emailService, EntityMapperService entityMapperService, AuthService authService) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.emailService = emailService;
        this.entityMapperService = entityMapperService;
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

        if (user.isEmailVerified()) {
            throw new BadRequestException("Email already verified");
        }

        tokenRepo.deleteByUserId(user.getId());

        String code = String.format("%06d", new Random().nextInt(999999));
        EmailVerificationToken token = new EmailVerificationToken(code, user, user.getEmail());
        tokenRepo.save(token);

        emailService.sendVerificationCode(user.getEmail(), code);
    }

    @Transactional
    public void verifyEmail(String code) {
        EmailVerificationToken token = tokenRepo.findByCodeAndUsedFalse(code).orElseThrow(() -> new ResourceNotFoundException("Invalid or already used code"));

        if (token.isExpired()) {
            throw new BadRequestException("Code has expired");
        }

        User user = token.getUser();
        user.setEmailVerified(true);
        token.setUsed(true);

        userRepo.save(user);
        tokenRepo.save(token);
        emailService.sendWelcome(user.getEmail(), user.getFirstName());
    }

    @Transactional
    public void requestEmailChange(String newEmail) {
        User user = authService.getCurrentUser();

        if (newEmail.equals(user.getEmail())) {
            throw new BadRequestException("New email is the same as current");
        }
        if (userRepo.existsByEmail(newEmail)) {
            throw new ConflictException("Email already in use");
        }

        tokenRepo.deleteByUserId(user.getId());

        String code = String.format("%06d", new Random().nextInt(999999));
        EmailVerificationToken token = new EmailVerificationToken(code, user, newEmail);
        tokenRepo.save(token);

        emailService.sendVerificationCode(newEmail, code);
    }

    @Transactional
    public void confirmEmailChange(String code) {
        User user = authService.getCurrentUser();
        EmailVerificationToken token = tokenRepo.findByCodeAndUsedFalse(code).orElseThrow(() -> new ResourceNotFoundException("Invalid or already used code"));

        if (!token.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Invalid code");
        }
        if (token.isExpired()) {
            throw new BadRequestException("Code has expired");
        }

        user.setEmail(token.getPendingEmail());
        user.setEmailVerified(true);
        token.setUsed(true);

        userRepo.save(user);
        tokenRepo.save(token);
    }
}
