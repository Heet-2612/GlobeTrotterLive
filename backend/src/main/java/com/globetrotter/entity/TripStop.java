package com.globetrotter.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "trip_stops")
public class TripStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public TripStop() {
    }

    public TripStop(Long id, Trip trip, City city, Integer stopOrder, LocalDate startDate, LocalDate endDate, String notes) {
        this.id = id;
        this.trip = trip;
        this.city = city;
        this.stopOrder = stopOrder;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
    }

    public static TripStopBuilder builder() {
        return new TripStopBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public Integer getStopOrder() { return stopOrder; }
    public void setStopOrder(Integer stopOrder) { this.stopOrder = stopOrder; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static class TripStopBuilder {
        private Long id;
        private Trip trip;
        private City city;
        private Integer stopOrder;
        private LocalDate startDate;
        private LocalDate endDate;
        private String notes;

        public TripStopBuilder id(Long id) { this.id = id; return this; }
        public TripStopBuilder trip(Trip trip) { this.trip = trip; return this; }
        public TripStopBuilder city(City city) { this.city = city; return this; }
        public TripStopBuilder stopOrder(Integer stopOrder) { this.stopOrder = stopOrder; return this; }
        public TripStopBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public TripStopBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public TripStopBuilder notes(String notes) { this.notes = notes; return this; }

        public TripStop build() {
            return new TripStop(id, trip, city, stopOrder, startDate, endDate, notes);
        }
    }
}
