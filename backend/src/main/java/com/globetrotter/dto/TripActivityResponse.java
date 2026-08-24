package com.globetrotter.dto;

import com.globetrotter.entity.TripActivity;

import java.time.LocalDate;
import java.time.LocalTime;

public class TripActivityResponse {

    private Long id;
    private Long tripStopId;
    private ActivityResponse activity;
    private LocalDate scheduledDate;
    private LocalTime startTime;
    private String notes;
    private Double customCost;
    private Integer activityOrder;

    public TripActivityResponse() {
    }

    public TripActivityResponse(Long id, Long tripStopId, ActivityResponse activity, LocalDate scheduledDate, LocalTime startTime, String notes, Double customCost, Integer activityOrder) {
        this.id = id;
        this.tripStopId = tripStopId;
        this.activity = activity;
        this.scheduledDate = scheduledDate;
        this.startTime = startTime;
        this.notes = notes;
        this.customCost = customCost;
        this.activityOrder = activityOrder;
    }

    public static TripActivityResponse fromEntity(TripActivity ta) {
        if (ta == null) return null;
        return new TripActivityResponse(
                ta.getId(),
                ta.getTripStop() != null ? ta.getTripStop().getId() : null,
                ActivityResponse.fromEntity(ta.getActivity()),
                ta.getScheduledDate(),
                ta.getStartTime(),
                ta.getNotes(),
                ta.getCustomCost() != null ? ta.getCustomCost() : (ta.getActivity() != null ? ta.getActivity().getEstimatedCost() : 0.0),
                ta.getActivityOrder()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTripStopId() { return tripStopId; }
    public void setTripStopId(Long tripStopId) { this.tripStopId = tripStopId; }

    public ActivityResponse getActivity() { return activity; }
    public void setActivity(ActivityResponse activity) { this.activity = activity; }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Double getCustomCost() { return customCost; }
    public void setCustomCost(Double customCost) { this.customCost = customCost; }

    public Integer getActivityOrder() { return activityOrder; }
    public void setActivityOrder(Integer activityOrder) { this.activityOrder = activityOrder; }
}
