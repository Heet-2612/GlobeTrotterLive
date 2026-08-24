package com.globetrotter.controller;

import com.globetrotter.dto.TripSharingResponse;
import com.globetrotter.dto.UpdateSharingRequest;
import com.globetrotter.entity.User;
import com.globetrotter.service.TripSharingService;
import com.globetrotter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips/{tripId}/sharing")
public class SharingController {

    private final TripSharingService tripSharingService;
    private final UserService userService;

    public SharingController(TripSharingService tripSharingService, UserService userService) {
        this.tripSharingService = tripSharingService;
        this.userService = userService;
    }

    @PutMapping
    public ResponseEntity<TripSharingResponse> updateSharing(
            @PathVariable Long tripId,
            @Valid @RequestBody UpdateSharingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        TripSharingResponse response = tripSharingService.updateSharing(tripId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<TripSharingResponse> getSharingStatus(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        TripSharingResponse response = tripSharingService.getSharingStatus(tripId, currentUser);
        return ResponseEntity.ok(response);
    }
}
