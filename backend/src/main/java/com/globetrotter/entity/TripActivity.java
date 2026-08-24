package com.globetrotter.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "trip_activities")
public class TripActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_stop_id", nullable = false)
    private TripStop tripStop;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "custom_cost")
    private Double customCost;

    @Column(name = "activity_order", nullable = false)
    private Integer activityOrder;

    public TripActivity() {
    }

    public TripActivity(Long id, TripStop tripStop, Activity activity, LocalDate scheduledDate, LocalTime startTime, String notes, Double customCost, Integer activityOrder) {
        this.id = id;
        this.tripStop = tripStop;
        this.activity = activity;
        this.scheduledDate = scheduledDate;
        this.startTime = startTime;
        this.notes = notes;
        this.customCost = customCost;
        this.activityOrder = activityOrder;
    }

    public static TripActivityBuilder builder() {
        return new TripActivityBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TripStop getTripStop() { return tripStop; }
    public void setTripStop(TripStop tripStop) { this.tripStop = tripStop; }

    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }

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

    public static class TripActivityBuilder {
        private Long id;
        private TripStop tripStop;
        private Activity activity;
        private LocalDate scheduledDate;
        private LocalTime startTime;
        private String notes;
        private Double customCost;
        private Integer activityOrder;

        public TripActivityBuilder id(Long id) { this.id = id; return this; }
        public TripActivityBuilder tripStop(TripStop tripStop) { this.tripStop = tripStop; return this; }
        public TripActivityBuilder activity(Activity activity) { this.activity = activity; return this; }
        public TripActivityBuilder scheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; return this; }
        public TripActivityBuilder startTime(LocalTime startTime) { this.startTime = startTime; return this; }
        public TripActivityBuilder notes(String notes) { this.notes = notes; return this; }
        public TripActivityBuilder customCost(Double customCost) { this.customCost = customCost; return this; }
        public TripActivityBuilder activityOrder(Integer activityOrder) { this.activityOrder = activityOrder; return this; }

        public TripActivity build() {
            return new TripActivity(id, tripStop, activity, scheduledDate, startTime, notes, customCost, activityOrder);
        }
    }
}
