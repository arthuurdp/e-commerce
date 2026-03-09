package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.domain.entities.PasswordVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordVerificationTokenRepository extends JpaRepository<PasswordVerificationToken, Long> {
    Optional<PasswordVerificationToken> findByCodeAndUsedFalse(String code);
    void deleteByUserId(Long userId);
}
