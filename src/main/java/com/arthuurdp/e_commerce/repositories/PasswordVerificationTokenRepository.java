package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.entities.PasswordVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordVerificationTokenRepository extends JpaRepository<PasswordVerificationToken, Long> {
    Optional<PasswordVerificationToken> findByCodeAndUsedFalse(String code);
    void deleteByUserId(Long userId);
}
