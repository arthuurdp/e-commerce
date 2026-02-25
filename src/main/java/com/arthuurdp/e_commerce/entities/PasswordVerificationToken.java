package com.arthuurdp.e_commerce.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_verification_tokens")
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

    public PasswordVerificationToken() {
    }

    public PasswordVerificationToken(String code, User user, String pendingPassword) {
        this.code = code;
        this.user = user;
        this.pendingPassword = pendingPassword;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPendingPassword() {
        return pendingPassword;
    }

    public void setPendingPassword(String pendingPassword) {
        this.pendingPassword = pendingPassword;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
