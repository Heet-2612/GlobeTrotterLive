package com.globetrotter.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "cover_photo", length = 500)
    private String coverPhoto;

    @Column(precision = 12, scale = 2)
    private BigDecimal budget;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "share_token", unique = true, length = 100)
    private String shareToken;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Trip() {
    }

    public Trip(Long id, User user, String name, String description, LocalDate startDate, LocalDate endDate, String coverPhoto, BigDecimal budget, Boolean isPublic, String shareToken, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coverPhoto = coverPhoto;
        this.budget = budget;
        this.isPublic = isPublic != null ? isPublic : false;
        this.shareToken = shareToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TripBuilder builder() {
        return new TripBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getCoverPhoto() { return coverPhoto; }
    public void setCoverPhoto(String coverPhoto) { this.coverPhoto = coverPhoto; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic != null ? isPublic : false; }

    public String getShareToken() { return shareToken; }
    public void setShareToken(String shareToken) { this.shareToken = shareToken; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class TripBuilder {
        private Long id;
        private User user;
        private String name;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private String coverPhoto;
        private BigDecimal budget;
        private Boolean isPublic = false;
        private String shareToken;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public TripBuilder id(Long id) { this.id = id; return this; }
        public TripBuilder user(User user) { this.user = user; return this; }
        public TripBuilder name(String name) { this.name = name; return this; }
        public TripBuilder description(String description) { this.description = description; return this; }
        public TripBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public TripBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public TripBuilder coverPhoto(String coverPhoto) { this.coverPhoto = coverPhoto; return this; }
        public TripBuilder budget(BigDecimal budget) { this.budget = budget; return this; }
        public TripBuilder isPublic(Boolean isPublic) { this.isPublic = isPublic; return this; }
        public TripBuilder shareToken(String shareToken) { this.shareToken = shareToken; return this; }
        public TripBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TripBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Trip build() {
            return new Trip(id, user, name, description, startDate, endDate, coverPhoto, budget, isPublic, shareToken, createdAt, updatedAt);
        }
    }
}
