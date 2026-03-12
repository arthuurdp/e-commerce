package com.arthuurdp.e_commerce.modules.email;

import com.arthuurdp.e_commerce.modules.email.entity.PasswordVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordVerificationTokenRepository extends JpaRepository<PasswordVerificationToken, Long> {
    Optional<PasswordVerificationToken> findByCodeAndUsedFalse(String code);
    void deleteByUserId(Long userId);
}
