package com.globetrotter.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "estimated_duration_minutes", nullable = false)
    private Integer estimatedDurationMinutes;

    @Column(nullable = false)
    private Double estimatedCost = 0.0;

    @Column(length = 10)
    private String currency = "INR";

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    public Activity() {
    }

    public Activity(Long id, City city, String name, String description, String category, Integer estimatedDurationMinutes, Double estimatedCost, String currency, String imageUrl) {
        this.id = id;
        this.city = city;
        this.name = name;
        this.description = description;
        this.category = category;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.estimatedCost = estimatedCost;
        this.currency = currency;
        this.imageUrl = imageUrl;
    }

    public static ActivityBuilder builder() {
        return new ActivityBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public Double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public static class ActivityBuilder {
        private Long id;
        private City city;
        private String name;
        private String description;
        private String category;
        private Integer estimatedDurationMinutes;
        private Double estimatedCost;
        private String currency;
        private String imageUrl;

        public ActivityBuilder id(Long id) { this.id = id; return this; }
        public ActivityBuilder city(City city) { this.city = city; return this; }
        public ActivityBuilder name(String name) { this.name = name; return this; }
        public ActivityBuilder description(String description) { this.description = description; return this; }
        public ActivityBuilder category(String category) { this.category = category; return this; }
        public ActivityBuilder estimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; return this; }
        public ActivityBuilder estimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; return this; }
        public ActivityBuilder currency(String currency) { this.currency = currency; return this; }
        public ActivityBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }

        public Activity build() {
            return new Activity(id, city, name, description, category, estimatedDurationMinutes, estimatedCost, currency, imageUrl);
        }
    }
}
