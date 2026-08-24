package com.globetrotter.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class PublicTripActivityResponse {

    private Long tripActivityId;
    private String activityName;
    private String category;
    private String description;
    private Integer durationMinutes;
    private Double cost;
    private LocalDate scheduledDate;
    private LocalTime startTime;
    private String notes;
    private Integer activityOrder;

    public PublicTripActivityResponse() {
    }

    public PublicTripActivityResponse(Long tripActivityId, String activityName, String category, String description, Integer durationMinutes, Double cost, LocalDate scheduledDate, LocalTime startTime, String notes, Integer activityOrder) {
        this.tripActivityId = tripActivityId;
        this.activityName = activityName;
        this.category = category;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.cost = cost;
        this.scheduledDate = scheduledDate;
        this.startTime = startTime;
        this.notes = notes;
        this.activityOrder = activityOrder;
    }

    public Long getTripActivityId() { return tripActivityId; }
    public void setTripActivityId(Long tripActivityId) { this.tripActivityId = tripActivityId; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getActivityOrder() { return activityOrder; }
    public void setActivityOrder(Integer activityOrder) { this.activityOrder = activityOrder; }
}
