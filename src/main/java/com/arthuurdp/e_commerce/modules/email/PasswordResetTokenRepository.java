package com.arthuurdp.e_commerce.modules.email;

import com.arthuurdp.e_commerce.modules.email.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByCodeAndUsedFalse(String code);
    void deleteByUserId(Long userId);
}
