package com.globetrotter.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class UpdateTripStopRequest {

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String notes;

    public UpdateTripStopRequest() {
    }

    public UpdateTripStopRequest(LocalDate startDate, LocalDate endDate, String notes) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
    }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
