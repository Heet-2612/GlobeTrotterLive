package com.globetrotter.service;

import com.globetrotter.dto.BudgetSummaryResponse;
import com.globetrotter.dto.CategoryCostSummary;
import com.globetrotter.dto.SetBudgetRequest;
import com.globetrotter.entity.Trip;
import com.globetrotter.entity.TripActivity;
import com.globetrotter.entity.TripStop;
import com.globetrotter.entity.User;
import com.globetrotter.exception.ResourceNotFoundException;
import com.globetrotter.repository.TripActivityRepository;
import com.globetrotter.repository.TripRepository;
import com.globetrotter.repository.TripStopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class BudgetService {

    private final TripRepository tripRepository;
    private final TripStopRepository tripStopRepository;
    private final TripActivityRepository tripActivityRepository;

    public BudgetService(TripRepository tripRepository, TripStopRepository tripStopRepository, TripActivityRepository tripActivityRepository) {
        this.tripRepository = tripRepository;
        this.tripStopRepository = tripStopRepository;
        this.tripActivityRepository = tripActivityRepository;
    }

    @Transactional
    public BudgetSummaryResponse setTripBudget(Long tripId, SetBudgetRequest request, User currentUser) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        if (request.getBudget() != null && request.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Budget must not be negative");
        }

        trip.setBudget(request.getBudget());
        tripRepository.save(trip);

        return calculateBudgetSummary(trip);
    }

    @Transactional(readOnly = true)
    public BudgetSummaryResponse getTripBudgetSummary(Long tripId, User currentUser) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        return calculateBudgetSummary(trip);
    }

    private BudgetSummaryResponse calculateBudgetSummary(Trip trip) {
        List<TripStop> stops = tripStopRepository.findByTripIdOrderByStopOrderAsc(trip.getId());

        BigDecimal totalActivityCost = BigDecimal.ZERO;
        Map<String, BigDecimal> categoryCostMap = new HashMap<>();
        Map<String, Integer> categoryCountMap = new HashMap<>();

        for (TripStop stop : stops) {
            List<TripActivity> activities = tripActivityRepository.findByTripStopIdOrderByActivityOrderAsc(stop.getId());
            for (TripActivity ta : activities) {
                Double costVal = ta.getCustomCost() != null ? ta.getCustomCost() : (ta.getActivity() != null ? ta.getActivity().getEstimatedCost() : 0.0);
                BigDecimal effectiveCost = BigDecimal.valueOf(costVal).setScale(2, RoundingMode.HALF_UP);

                totalActivityCost = totalActivityCost.add(effectiveCost);

                String category = (ta.getActivity() != null && ta.getActivity().getCategory() != null) ? ta.getActivity().getCategory() : "OTHER";
                categoryCostMap.put(category, categoryCostMap.getOrDefault(category, BigDecimal.ZERO).add(effectiveCost));
                categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
            }
        }

        totalActivityCost = totalActivityCost.setScale(2, RoundingMode.HALF_UP);

        BigDecimal budget = trip.getBudget() != null ? trip.getBudget().setScale(2, RoundingMode.HALF_UP) : null;
        BigDecimal remainingBudget = null;
        Double budgetUsedPercentage = null;
        Boolean budgetExceeded = false;

        if (budget != null) {
            remainingBudget = budget.subtract(totalActivityCost).setScale(2, RoundingMode.HALF_UP);
            if (budget.compareTo(BigDecimal.ZERO) > 0) {
                budgetUsedPercentage = totalActivityCost.multiply(BigDecimal.valueOf(100))
                        .divide(budget, 2, RoundingMode.HALF_UP)
                        .doubleValue();
            } else {
                budgetUsedPercentage = totalActivityCost.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
            }
            budgetExceeded = totalActivityCost.compareTo(budget) > 0;
        }

        List<CategoryCostSummary> breakdown = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : categoryCostMap.entrySet()) {
            breakdown.add(new CategoryCostSummary(entry.getKey(), entry.getValue().setScale(2, RoundingMode.HALF_UP), categoryCountMap.get(entry.getKey())));
        }
        breakdown.sort(Comparator.comparing(CategoryCostSummary::getCategory));

        return new BudgetSummaryResponse(
                trip.getId(),
                budget,
                totalActivityCost,
                remainingBudget,
                budgetUsedPercentage,
                budgetExceeded,
                "INR",
                breakdown
        );
    }
}
