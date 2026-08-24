package com.globetrotter.dto;

import com.globetrotter.entity.Activity;

public class ActivityResponse {

    private Long id;
    private Long cityId;
    private String cityName;
    private String name;
    private String description;
    private String category;
    private Integer estimatedDurationMinutes;
    private Double estimatedCost;
    private String currency;
    private String imageUrl;

    public ActivityResponse() {
    }

    public ActivityResponse(Long id, Long cityId, String cityName, String name, String description, String category, Integer estimatedDurationMinutes, Double estimatedCost, String currency, String imageUrl) {
        this.id = id;
        this.cityId = cityId;
        this.cityName = cityName;
        this.name = name;
        this.description = description;
        this.category = category;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.estimatedCost = estimatedCost;
        this.currency = currency;
        this.imageUrl = imageUrl;
    }

    public static ActivityResponse fromEntity(Activity activity) {
        if (activity == null) return null;
        return new ActivityResponse(
                activity.getId(),
                activity.getCity() != null ? activity.getCity().getId() : null,
                activity.getCity() != null ? activity.getCity().getName() : null,
                activity.getName(),
                activity.getDescription(),
                activity.getCategory(),
                activity.getEstimatedDurationMinutes(),
                activity.getEstimatedCost(),
                activity.getCurrency(),
                activity.getImageUrl()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCityId() { return cityId; }
    public void setCityId(Long cityId) { this.cityId = cityId; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

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
}
