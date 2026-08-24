package com.globetrotter.dto;

import com.globetrotter.entity.TripStop;

import java.time.LocalDate;

public class TripStopResponse {

    private Long id;
    private Long tripId;
    private CityResponse city;
    private Integer stopOrder;
    private LocalDate startDate;
    private LocalDate endDate;
    private String notes;

    public TripStopResponse() {
    }

    public TripStopResponse(Long id, Long tripId, CityResponse city, Integer stopOrder, LocalDate startDate, LocalDate endDate, String notes) {
        this.id = id;
        this.tripId = tripId;
        this.city = city;
        this.stopOrder = stopOrder;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
    }

    public static TripStopResponse fromEntity(TripStop stop) {
        if (stop == null) return null;
        return new TripStopResponse(
                stop.getId(),
                stop.getTrip() != null ? stop.getTrip().getId() : null,
                CityResponse.fromEntity(stop.getCity()),
                stop.getStopOrder(),
                stop.getStartDate(),
                stop.getEndDate(),
                stop.getNotes()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public CityResponse getCity() { return city; }
    public void setCity(CityResponse city) { this.city = city; }

    public Integer getStopOrder() { return stopOrder; }
    public void setStopOrder(Integer stopOrder) { this.stopOrder = stopOrder; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
