package com.globetrotter.controller;

import com.globetrotter.dto.*;
import com.globetrotter.entity.User;
import com.globetrotter.service.TripActivityService;
import com.globetrotter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/stops/{stopId}/activities")
public class TripActivityController {

    private final TripActivityService tripActivityService;
    private final UserService userService;

    public TripActivityController(TripActivityService tripActivityService, UserService userService) {
        this.tripActivityService = tripActivityService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TripActivityResponse> createTripActivity(
            @PathVariable Long tripId,
            @PathVariable Long stopId,
            @Valid @RequestBody CreateTripActivityRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        TripActivityResponse response = tripActivityService.createTripActivity(tripId, stopId, request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TripActivityResponse>> getTripActivities(
            @PathVariable Long tripId,
            @PathVariable Long stopId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        List<TripActivityResponse> response = tripActivityService.getTripActivities(tripId, stopId, currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{tripActivityId}")
    public ResponseEntity<TripActivityResponse> updateTripActivity(
            @PathVariable Long tripId,
            @PathVariable Long stopId,
            @PathVariable Long tripActivityId,
            @Valid @RequestBody UpdateTripActivityRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        TripActivityResponse response = tripActivityService.updateTripActivity(tripId, stopId, tripActivityId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tripActivityId}")
    public ResponseEntity<Void> deleteTripActivity(
            @PathVariable Long tripId,
            @PathVariable Long stopId,
            @PathVariable Long tripActivityId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        tripActivityService.deleteTripActivity(tripId, stopId, tripActivityId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reorder")
    public ResponseEntity<List<TripActivityResponse>> reorderTripActivities(
            @PathVariable Long tripId,
            @PathVariable Long stopId,
            @Valid @RequestBody ReorderTripActivitiesRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        List<TripActivityResponse> response = tripActivityService.reorderTripActivities(tripId, stopId, request, currentUser);
        return ResponseEntity.ok(response);
    }
}
