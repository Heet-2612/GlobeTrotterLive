package com.globetrotter.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public PasswordResetToken() {
    }

    public PasswordResetToken(Long id, User user, String tokenHash, Instant expiresAt, Instant usedAt, Instant createdAt) {
        this.id = id;
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public Instant getUsedAt() { return usedAt; }
    public void setUsedAt(Instant usedAt) { this.usedAt = usedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static PasswordResetTokenBuilder builder() {
        return new PasswordResetTokenBuilder();
    }

    public static class PasswordResetTokenBuilder {
        private Long id;
        private User user;
        private String tokenHash;
        private Instant expiresAt;
        private Instant usedAt;
        private Instant createdAt;

        public PasswordResetTokenBuilder id(Long id) { this.id = id; return this; }
        public PasswordResetTokenBuilder user(User user) { this.user = user; return this; }
        public PasswordResetTokenBuilder tokenHash(String tokenHash) { this.tokenHash = tokenHash; return this; }
        public PasswordResetTokenBuilder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public PasswordResetTokenBuilder usedAt(Instant usedAt) { this.usedAt = usedAt; return this; }
        public PasswordResetTokenBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public PasswordResetToken build() {
            return new PasswordResetToken(id, user, tokenHash, expiresAt, usedAt, createdAt);
        }
    }
}
