package com.globetrotter.controller;

import com.globetrotter.dto.CreateTripStopRequest;
import com.globetrotter.dto.ReorderStopsRequest;
import com.globetrotter.dto.TripStopResponse;
import com.globetrotter.dto.UpdateTripStopRequest;
import com.globetrotter.entity.User;
import com.globetrotter.service.TripStopService;
import com.globetrotter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/stops")
public class TripStopController {

    private final TripStopService tripStopService;
    private final UserService userService;

    public TripStopController(TripStopService tripStopService, UserService userService) {
        this.tripStopService = tripStopService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TripStopResponse> createTripStop(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateTripStopRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        TripStopResponse response = tripStopService.createTripStop(tripId, request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TripStopResponse>> getTripStops(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        List<TripStopResponse> response = tripStopService.getTripStops(tripId, currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{stopId}")
    public ResponseEntity<TripStopResponse> updateTripStop(
            @PathVariable Long tripId,
            @PathVariable Long stopId,
            @Valid @RequestBody UpdateTripStopRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        TripStopResponse response = tripStopService.updateTripStop(tripId, stopId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{stopId}")
    public ResponseEntity<Void> deleteTripStop(
            @PathVariable Long tripId,
            @PathVariable Long stopId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        tripStopService.deleteTripStop(tripId, stopId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reorder")
    public ResponseEntity<List<TripStopResponse>> reorderTripStops(
            @PathVariable Long tripId,
            @Valid @RequestBody ReorderStopsRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        List<TripStopResponse> response = tripStopService.reorderTripStops(tripId, request, currentUser);
        return ResponseEntity.ok(response);
    }
}
