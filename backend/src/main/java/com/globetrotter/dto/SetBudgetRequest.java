package com.globetrotter.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class SetBudgetRequest {

    @NotNull(message = "Budget is required")
    @DecimalMin(value = "0.0", message = "Budget must not be negative")
    private BigDecimal budget;

    public SetBudgetRequest() {
    }

    public SetBudgetRequest(BigDecimal budget) {
        this.budget = budget;
    }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
}
