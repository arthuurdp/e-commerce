package com.arthuurdp.e_commerce.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_verification_tokens")
@Getter
@Setter
@NoArgsConstructor
public class PasswordVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "pending_password", nullable = false)
    private String pendingPassword;

    @Column(name = "expiresAt", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used = false;

    @PrePersist void setExpiresAt() {
        this.expiresAt = LocalDateTime.now().plusMinutes(15);
    }

    public PasswordVerificationToken(String code, User user, String pendingPassword) {
        this.code = code;
        this.user = user;
        this.pendingPassword = pendingPassword;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
