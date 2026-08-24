package com.globetrotter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class UpdateTripActivityRequest {

    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    private LocalTime startTime;

    private String notes;

    @Min(value = 0, message = "Custom cost must not be negative")
    private Double customCost;

    public UpdateTripActivityRequest() {
    }

    public UpdateTripActivityRequest(LocalDate scheduledDate, LocalTime startTime, String notes, Double customCost) {
        this.scheduledDate = scheduledDate;
        this.startTime = startTime;
        this.notes = notes;
        this.customCost = customCost;
    }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Double getCustomCost() { return customCost; }
    public void setCustomCost(Double customCost) { this.customCost = customCost; }
}
