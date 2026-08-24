package com.globetrotter.dto;

import java.math.BigDecimal;
import java.util.List;

public class BudgetSummaryResponse {

    private Long tripId;
    private BigDecimal budget;
    private BigDecimal totalActivityCost;
    private BigDecimal remainingBudget;
    private Double budgetUsedPercentage;
    private Boolean budgetExceeded;
    private String currency;
    private List<CategoryCostSummary> categoryBreakdown;

    public BudgetSummaryResponse() {
    }

    public BudgetSummaryResponse(Long tripId, BigDecimal budget, BigDecimal totalActivityCost, BigDecimal remainingBudget, Double budgetUsedPercentage, Boolean budgetExceeded, String currency, List<CategoryCostSummary> categoryBreakdown) {
        this.tripId = tripId;
        this.budget = budget;
        this.totalActivityCost = totalActivityCost;
        this.remainingBudget = remainingBudget;
        this.budgetUsedPercentage = budgetUsedPercentage;
        this.budgetExceeded = budgetExceeded;
        this.currency = currency;
        this.categoryBreakdown = categoryBreakdown;
    }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    public BigDecimal getTotalActivityCost() { return totalActivityCost; }
    public void setTotalActivityCost(BigDecimal totalActivityCost) { this.totalActivityCost = totalActivityCost; }

    public BigDecimal getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(BigDecimal remainingBudget) { this.remainingBudget = remainingBudget; }

    public Double getBudgetUsedPercentage() { return budgetUsedPercentage; }
    public void setBudgetUsedPercentage(Double budgetUsedPercentage) { this.budgetUsedPercentage = budgetUsedPercentage; }

    public Boolean getBudgetExceeded() { return budgetExceeded; }
    public void setBudgetExceeded(Boolean budgetExceeded) { this.budgetExceeded = budgetExceeded; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public List<CategoryCostSummary> getCategoryBreakdown() { return categoryBreakdown; }
    public void setCategoryBreakdown(List<CategoryCostSummary> categoryBreakdown) { this.categoryBreakdown = categoryBreakdown; }
}
