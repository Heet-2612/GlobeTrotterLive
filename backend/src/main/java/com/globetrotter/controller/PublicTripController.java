package com.globetrotter.controller;

import com.globetrotter.dto.PublicTripItineraryResponse;
import com.globetrotter.dto.TripResponse;
import com.globetrotter.entity.User;
import com.globetrotter.service.TripSharingService;
import com.globetrotter.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/trips")
public class PublicTripController {

    private final TripSharingService tripSharingService;
    private final UserService userService;

    public PublicTripController(TripSharingService tripSharingService, UserService userService) {
        this.tripSharingService = tripSharingService;
        this.userService = userService;
    }

    @GetMapping("/{shareToken}")
    public ResponseEntity<PublicTripItineraryResponse> getPublicTripItinerary(@PathVariable String shareToken) {
        PublicTripItineraryResponse response = tripSharingService.getPublicTripItinerary(shareToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{shareToken}/copy")
    public ResponseEntity<TripResponse> copyPublicTrip(
            @PathVariable String shareToken,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        TripResponse response = tripSharingService.copyPublicTrip(shareToken, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
