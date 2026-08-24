package com.globetrotter.controller;

import com.globetrotter.dto.AdminStatsResponse;
import com.globetrotter.dto.AdminTripResponse;
import com.globetrotter.dto.UserResponse;
import com.globetrotter.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUser(id));
    }

    @PostMapping("/users/{id}/promote")
    public ResponseEntity<UserResponse> promoteUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.promoteUser(id));
    }

    @PostMapping("/users/{id}/demote")
    public ResponseEntity<UserResponse> demoteUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.demoteUser(id));
    }

    @GetMapping("/trips")
    public ResponseEntity<List<AdminTripResponse>> getAllTrips() {
        return ResponseEntity.ok(adminService.getAllTrips());
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}
