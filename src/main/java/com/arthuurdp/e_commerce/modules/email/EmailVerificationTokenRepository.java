package com.arthuurdp.e_commerce.modules.email;

import com.arthuurdp.e_commerce.modules.email.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByCodeAndUsedFalse(String code);
    void deleteByUserId(Long userId);
}
