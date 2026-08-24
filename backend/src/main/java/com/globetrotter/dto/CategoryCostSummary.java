package com.globetrotter.dto;

import java.math.BigDecimal;

public class CategoryCostSummary {

    private String category;
    private BigDecimal cost;
    private Integer activityCount;

    public CategoryCostSummary() {
    }

    public CategoryCostSummary(String category, BigDecimal cost, Integer activityCount) {
        this.category = category;
        this.cost = cost;
        this.activityCount = activityCount;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public Integer getActivityCount() { return activityCount; }
    public void setActivityCount(Integer activityCount) { this.activityCount = activityCount; }
}
