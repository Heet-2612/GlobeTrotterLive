package com.globetrotter.dto;

import com.globetrotter.entity.Trip;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminTripResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budget;
    private LocalDateTime createdAt;

    public AdminTripResponse() {}

    public static AdminTripResponse fromEntity(Trip trip) {
        if (trip == null) return null;
        AdminTripResponse response = new AdminTripResponse();
        response.setId(trip.getId());
        response.setName(trip.getName());
        response.setStartDate(trip.getStartDate());
        response.setEndDate(trip.getEndDate());
        response.setBudget(trip.getBudget());
        response.setCreatedAt(trip.getCreatedAt());
        if (trip.getUser() != null) {
            response.setUserId(trip.getUser().getId());
            response.setUserName(trip.getUser().getName());
            response.setUserEmail(trip.getUser().getEmail());
        }
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
