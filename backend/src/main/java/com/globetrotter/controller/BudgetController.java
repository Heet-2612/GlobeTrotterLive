package com.globetrotter.controller;

import com.globetrotter.dto.BudgetSummaryResponse;
import com.globetrotter.dto.SetBudgetRequest;
import com.globetrotter.entity.User;
import com.globetrotter.service.BudgetService;
import com.globetrotter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips/{tripId}/budget")
public class BudgetController {

    private final BudgetService budgetService;
    private final UserService userService;

    public BudgetController(BudgetService budgetService, UserService userService) {
        this.budgetService = budgetService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<BudgetSummaryResponse> getTripBudgetSummary(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        BudgetSummaryResponse response = budgetService.getTripBudgetSummary(tripId, currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<BudgetSummaryResponse> setTripBudget(
            @PathVariable Long tripId,
            @Valid @RequestBody SetBudgetRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        BudgetSummaryResponse response = budgetService.setTripBudget(tripId, request, currentUser);
        return ResponseEntity.ok(response);
    }
}
