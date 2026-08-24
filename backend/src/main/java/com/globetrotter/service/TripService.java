package com.globetrotter.service;

import com.globetrotter.dto.CreateTripRequest;
import com.globetrotter.dto.TripResponse;
import com.globetrotter.dto.UpdateTripRequest;
import com.globetrotter.entity.Trip;
import com.globetrotter.entity.User;
import com.globetrotter.exception.ResourceNotFoundException;
import com.globetrotter.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Transactional
    public TripResponse createTrip(CreateTripRequest request, User currentUser) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (request.getBudget() != null && request.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Budget must not be negative");
        }

        Trip trip = Trip.builder()
                .user(currentUser)
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .coverPhoto(request.getCoverPhoto())
                .budget(request.getBudget())
                .build();

        Trip savedTrip = tripRepository.save(trip);
        return TripResponse.fromEntity(savedTrip);
    }

    @Transactional(readOnly = true)
    public List<TripResponse> getUserTrips(User currentUser) {
        return tripRepository.findByUserIdOrderByStartDateAsc(currentUser.getId())
                .stream()
                .map(TripResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TripResponse getTripById(Long tripId, User currentUser) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));
        return TripResponse.fromEntity(trip);
    }

    @Transactional
    public TripResponse updateTrip(Long tripId, UpdateTripRequest request, User currentUser) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (request.getBudget() != null && request.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Budget must not be negative");
        }

        trip.setName(request.getName());
        trip.setDescription(request.getDescription());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        trip.setCoverPhoto(request.getCoverPhoto());
        trip.setBudget(request.getBudget());

        Trip updatedTrip = tripRepository.save(trip);
        return TripResponse.fromEntity(updatedTrip);
    }

    @Transactional
    public void deleteTrip(Long tripId, User currentUser) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));
        tripRepository.delete(trip);
    }
}
