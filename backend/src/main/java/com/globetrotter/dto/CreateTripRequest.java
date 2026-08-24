package com.globetrotter.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateTripRequest {

    @NotBlank(message = "Trip name is required")
    @Size(max = 150, message = "Trip name must not exceed 150 characters")
    private String name;

    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Size(max = 500, message = "Cover photo URL must not exceed 500 characters")
    private String coverPhoto;

    @DecimalMin(value = "0.0", message = "Budget must not be negative")
    private BigDecimal budget;

    public CreateTripRequest() {
    }

    public CreateTripRequest(String name, String description, LocalDate startDate, LocalDate endDate, String coverPhoto) {
        this(name, description, startDate, endDate, coverPhoto, null);
    }

    public CreateTripRequest(String name, String description, LocalDate startDate, LocalDate endDate, String coverPhoto, BigDecimal budget) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coverPhoto = coverPhoto;
        this.budget = budget;
    }

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
}
