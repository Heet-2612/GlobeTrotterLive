package com.globetrotter.service;

import com.globetrotter.dto.*;
import com.globetrotter.entity.*;
import com.globetrotter.exception.ResourceNotFoundException;
import com.globetrotter.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TripActivityService {

    private final TripActivityRepository tripActivityRepository;
    private final TripStopRepository tripStopRepository;
    private final TripRepository tripRepository;
    private final ActivityRepository activityRepository;

    public TripActivityService(
            TripActivityRepository tripActivityRepository,
            TripStopRepository tripStopRepository,
            TripRepository tripRepository,
            ActivityRepository activityRepository
    ) {
        this.tripActivityRepository = tripActivityRepository;
        this.tripStopRepository = tripStopRepository;
        this.tripRepository = tripRepository;
        this.activityRepository = activityRepository;
    }

    private TripStop getOwnedTripStop(Long tripId, Long stopId, User currentUser) {
        tripRepository.findByIdAndUserId(tripId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        return tripStopRepository.findByIdAndTripId(stopId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip stop not found with id: " + stopId));
    }

    @Transactional
    public TripActivityResponse createTripActivity(Long tripId, Long stopId, CreateTripActivityRequest request, User currentUser) {
        TripStop stop = getOwnedTripStop(tripId, stopId, currentUser);

        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id: " + request.getActivityId()));

        if (!activity.getCity().getId().equals(stop.getCity().getId())) {
            throw new IllegalArgumentException("Activity must belong to the same city as the trip stop (" + stop.getCity().getName() + ")");
        }

        if (request.getScheduledDate().isBefore(stop.getStartDate()) || request.getScheduledDate().isAfter(stop.getEndDate())) {
            throw new IllegalArgumentException("Scheduled date must fall within the trip stop's date range (" + stop.getStartDate() + " to " + stop.getEndDate() + ")");
        }

        if (request.getCustomCost() != null && request.getCustomCost() < 0) {
            throw new IllegalArgumentException("Custom cost must not be negative");
        }

        int nextOrder = (int) tripActivityRepository.countByTripStopId(stopId) + 1;

        Double cost = request.getCustomCost() != null ? request.getCustomCost() : activity.getEstimatedCost();

        TripActivity tripActivity = TripActivity.builder()
                .tripStop(stop)
                .activity(activity)
                .scheduledDate(request.getScheduledDate())
                .startTime(request.getStartTime())
                .notes(request.getNotes())
                .customCost(cost)
                .activityOrder(nextOrder)
                .build();

        TripActivity saved = tripActivityRepository.save(tripActivity);
        return TripActivityResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<TripActivityResponse> getTripActivities(Long tripId, Long stopId, User currentUser) {
        getOwnedTripStop(tripId, stopId, currentUser);
        return tripActivityRepository.findByTripStopIdOrderByActivityOrderAsc(stopId)
                .stream()
                .map(TripActivityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TripActivityResponse updateTripActivity(Long tripId, Long stopId, Long tripActivityId, UpdateTripActivityRequest request, User currentUser) {
        TripStop stop = getOwnedTripStop(tripId, stopId, currentUser);

        TripActivity tripActivity = tripActivityRepository.findByIdAndTripStopId(tripActivityId, stopId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip activity not found with id: " + tripActivityId));

        if (request.getScheduledDate().isBefore(stop.getStartDate()) || request.getScheduledDate().isAfter(stop.getEndDate())) {
            throw new IllegalArgumentException("Scheduled date must fall within the trip stop's date range (" + stop.getStartDate() + " to " + stop.getEndDate() + ")");
        }

        if (request.getCustomCost() != null && request.getCustomCost() < 0) {
            throw new IllegalArgumentException("Custom cost must not be negative");
        }

        tripActivity.setScheduledDate(request.getScheduledDate());
        tripActivity.setStartTime(request.getStartTime());
        tripActivity.setNotes(request.getNotes());
        if (request.getCustomCost() != null) {
            tripActivity.setCustomCost(request.getCustomCost());
        }

        TripActivity updated = tripActivityRepository.save(tripActivity);
        return TripActivityResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteTripActivity(Long tripId, Long stopId, Long tripActivityId, User currentUser) {
        getOwnedTripStop(tripId, stopId, currentUser);

        TripActivity tripActivity = tripActivityRepository.findByIdAndTripStopId(tripActivityId, stopId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip activity not found with id: " + tripActivityId));

        tripActivityRepository.delete(tripActivity);

        // Re-sequence remaining activities
        List<TripActivity> remaining = tripActivityRepository.findByTripStopIdOrderByActivityOrderAsc(stopId);
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setActivityOrder(i + 1);
        }
        tripActivityRepository.saveAll(remaining);
    }

    @Transactional
    public List<TripActivityResponse> reorderTripActivities(Long tripId, Long stopId, ReorderTripActivitiesRequest request, User currentUser) {
        getOwnedTripStop(tripId, stopId, currentUser);

        List<TripActivity> existing = tripActivityRepository.findByTripStopIdOrderByActivityOrderAsc(stopId);
        List<Long> requestedOrder = request.getTripActivityIds();

        if (existing.size() != requestedOrder.size()) {
            throw new IllegalArgumentException("Reorder activity IDs count does not match existing trip activities count");
        }

        Map<Long, TripActivity> taMap = existing.stream()
                .collect(Collectors.toMap(TripActivity::getId, Function.identity()));

        for (int i = 0; i < requestedOrder.size(); i++) {
            Long taId = requestedOrder.get(i);
            TripActivity ta = taMap.get(taId);
            if (ta == null) {
                throw new IllegalArgumentException("Trip activity with id " + taId + " does not belong to trip stop " + stopId);
            }
            ta.setActivityOrder(i + 1);
        }

        List<TripActivity> saved = tripActivityRepository.saveAll(existing);
        return saved.stream()
                .sorted((a, b) -> Integer.compare(a.getActivityOrder(), b.getActivityOrder()))
                .map(TripActivityResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
