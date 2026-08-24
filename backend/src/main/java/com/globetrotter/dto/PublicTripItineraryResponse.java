package com.globetrotter.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PublicTripItineraryResponse {

    private Long tripId;
    private String shareToken;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String coverPhoto;
    private String creatorName;
    private BigDecimal budget;
    private List<PublicTripStopResponse> stops;

    public PublicTripItineraryResponse() {
    }

    public PublicTripItineraryResponse(Long tripId, String shareToken, String name, String description, LocalDate startDate, LocalDate endDate, String coverPhoto, String creatorName, BigDecimal budget, List<PublicTripStopResponse> stops) {
        this.tripId = tripId;
        this.shareToken = shareToken;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coverPhoto = coverPhoto;
        this.creatorName = creatorName;
        this.budget = budget;
        this.stops = stops;
    }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getShareToken() { return shareToken; }
    public void setShareToken(String shareToken) { this.shareToken = shareToken; }

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

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    public List<PublicTripStopResponse> getStops() { return stops; }
    public void setStops(List<PublicTripStopResponse> stops) { this.stops = stops; }
}
