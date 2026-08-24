package com.globetrotter.service;

import com.globetrotter.dto.AdminStatsResponse;
import com.globetrotter.dto.AdminTripResponse;
import com.globetrotter.dto.UserResponse;
import com.globetrotter.entity.Role;
import com.globetrotter.entity.User;
import com.globetrotter.exception.ResourceNotFoundException;
import com.globetrotter.repository.TripRepository;
import com.globetrotter.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    public AdminService(UserRepository userRepository, TripRepository tripRepository) {
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public List<AdminTripResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(AdminTripResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long totalTrips = tripRepository.count();
        
        // Count users created in the last 30 days and admin users
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long newUsers = userRepository.findAll().stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();
        long adminUsers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .count();

        return new AdminStatsResponse(totalUsers, totalTrips, newUsers, adminUsers);
    }

    @Transactional
    public UserResponse promoteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(Role.ADMIN);
        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse demoteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Prevent demoting the last admin
        long adminCount = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .count();
        
        if (adminCount <= 1 && user.getRole() == Role.ADMIN) {
            throw new IllegalStateException("Cannot demote the last administrator.");
        }
        
        user.setRole(Role.USER);
        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }
}
