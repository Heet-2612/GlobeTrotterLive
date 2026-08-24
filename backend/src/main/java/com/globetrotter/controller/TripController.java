package com.globetrotter.controller;

import com.globetrotter.dto.CreateTripRequest;
import com.globetrotter.dto.TripResponse;
import com.globetrotter.dto.UpdateTripRequest;
import com.globetrotter.entity.User;
import com.globetrotter.service.TripService;
import com.globetrotter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;
    private final UserService userService;

    public TripController(TripService tripService, UserService userService) {
        this.tripService = tripService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @Valid @RequestBody CreateTripRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        TripResponse response = tripService.createTrip(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> getUserTrips(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        List<TripResponse> response = tripService.getUserTrips(currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTripById(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        TripResponse response = tripService.getTripById(tripId, currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable Long tripId,
            @Valid @RequestBody UpdateTripRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        TripResponse response = tripService.updateTrip(tripId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteTrip(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        tripService.deleteTrip(tripId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
