package com.globetrotter.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ReorderTripActivitiesRequest {

    @NotEmpty(message = "Trip activity IDs list must not be empty")
    private List<Long> tripActivityIds;

    public ReorderTripActivitiesRequest() {
    }

    public ReorderTripActivitiesRequest(List<Long> tripActivityIds) {
        this.tripActivityIds = tripActivityIds;
    }

    public List<Long> getTripActivityIds() { return tripActivityIds; }
    public void setTripActivityIds(List<Long> tripActivityIds) { this.tripActivityIds = tripActivityIds; }
}
