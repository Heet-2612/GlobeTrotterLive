package com.globetrotter.dto;

import java.time.LocalDate;
import java.util.List;

public class PublicTripStopResponse {

    private Long stopId;
    private CityResponse city;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer stopOrder;
    private String notes;
    private List<PublicTripActivityResponse> activities;

    public PublicTripStopResponse() {
    }

    public PublicTripStopResponse(Long stopId, CityResponse city, LocalDate startDate, LocalDate endDate, Integer stopOrder, String notes, List<PublicTripActivityResponse> activities) {
        this.stopId = stopId;
        this.city = city;
        this.startDate = startDate;
        this.endDate = endDate;
        this.stopOrder = stopOrder;
        this.notes = notes;
        this.activities = activities;
    }

    public Long getStopId() { return stopId; }
    public void setStopId(Long stopId) { this.stopId = stopId; }

    public CityResponse getCity() { return city; }
    public void setCity(CityResponse city) { this.city = city; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getStopOrder() { return stopOrder; }
    public void setStopOrder(Integer stopOrder) { this.stopOrder = stopOrder; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<PublicTripActivityResponse> getActivities() { return activities; }
    public void setActivities(List<PublicTripActivityResponse> activities) { this.activities = activities; }
}
