package com.globetrotter.service;

import com.globetrotter.dto.CreateTripStopRequest;
import com.globetrotter.dto.ReorderStopsRequest;
import com.globetrotter.dto.TripStopResponse;
import com.globetrotter.dto.UpdateTripStopRequest;
import com.globetrotter.entity.City;
import com.globetrotter.entity.Trip;
import com.globetrotter.entity.TripStop;
import com.globetrotter.entity.User;
import com.globetrotter.exception.ResourceNotFoundException;
import com.globetrotter.repository.CityRepository;
import com.globetrotter.repository.TripRepository;
import com.globetrotter.repository.TripStopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TripStopService {

    private final TripStopRepository tripStopRepository;
    private final TripRepository tripRepository;
    private final CityRepository cityRepository;

    public TripStopService(TripStopRepository tripStopRepository, TripRepository tripRepository, CityRepository cityRepository) {
        this.tripStopRepository = tripStopRepository;
        this.tripRepository = tripRepository;
        this.cityRepository = cityRepository;
    }

    private Trip getOwnedTrip(Long tripId, User currentUser) {
        return tripRepository.findByIdAndUserId(tripId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));
    }

    @Transactional
    public TripStopResponse createTripStop(Long tripId, CreateTripStopRequest request, User currentUser) {
        Trip trip = getOwnedTrip(tripId, currentUser);

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (request.getStartDate().isBefore(trip.getStartDate()) || request.getEndDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("Stop dates must fall within the trip's date range (" + trip.getStartDate() + " to " + trip.getEndDate() + ")");
        }

        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + request.getCityId()));

        int nextOrder = (int) tripStopRepository.countByTripId(tripId) + 1;

        TripStop stop = TripStop.builder()
                .trip(trip)
                .city(city)
                .stopOrder(nextOrder)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .notes(request.getNotes())
                .build();

        TripStop savedStop = tripStopRepository.save(stop);
        return TripStopResponse.fromEntity(savedStop);
    }

    @Transactional(readOnly = true)
    public List<TripStopResponse> getTripStops(Long tripId, User currentUser) {
        getOwnedTrip(tripId, currentUser);
        return tripStopRepository.findByTripIdOrderByStopOrderAsc(tripId)
                .stream()
                .map(TripStopResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TripStopResponse updateTripStop(Long tripId, Long stopId, UpdateTripStopRequest request, User currentUser) {
        Trip trip = getOwnedTrip(tripId, currentUser);

        TripStop stop = tripStopRepository.findByIdAndTripId(stopId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip stop not found with id: " + stopId));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (request.getStartDate().isBefore(trip.getStartDate()) || request.getEndDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("Stop dates must fall within the trip's date range (" + trip.getStartDate() + " to " + trip.getEndDate() + ")");
        }

        stop.setStartDate(request.getStartDate());
        stop.setEndDate(request.getEndDate());
        stop.setNotes(request.getNotes());

        TripStop updatedStop = tripStopRepository.save(stop);
        return TripStopResponse.fromEntity(updatedStop);
    }

    @Transactional
    public void deleteTripStop(Long tripId, Long stopId, User currentUser) {
        getOwnedTrip(tripId, currentUser);

        TripStop stop = tripStopRepository.findByIdAndTripId(stopId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip stop not found with id: " + stopId));

        tripStopRepository.delete(stop);

        // Re-sequence remaining stops for consistency
        List<TripStop> remainingStops = tripStopRepository.findByTripIdOrderByStopOrderAsc(tripId);
        for (int i = 0; i < remainingStops.size(); i++) {
            remainingStops.get(i).setStopOrder(i + 1);
        }
        tripStopRepository.saveAll(remainingStops);
    }

    @Transactional
    public List<TripStopResponse> reorderTripStops(Long tripId, ReorderStopsRequest request, User currentUser) {
        getOwnedTrip(tripId, currentUser);

        List<TripStop> existingStops = tripStopRepository.findByTripIdOrderByStopOrderAsc(tripId);
        List<Long> requestedOrder = request.getStopIds();

        if (existingStops.size() != requestedOrder.size()) {
            throw new IllegalArgumentException("Reorder stop IDs count does not match existing trip stops count");
        }

        Map<Long, TripStop> stopMap = existingStops.stream()
                .collect(Collectors.toMap(TripStop::getId, Function.identity()));

        for (int i = 0; i < requestedOrder.size(); i++) {
            Long stopId = requestedOrder.get(i);
            TripStop stop = stopMap.get(stopId);
            if (stop == null) {
                throw new IllegalArgumentException("Trip stop with id " + stopId + " does not belong to trip " + tripId);
            }
            stop.setStopOrder(i + 1);
        }

        List<TripStop> savedStops = tripStopRepository.saveAll(existingStops);
        return savedStops.stream()
                .sorted((a, b) -> Integer.compare(a.getStopOrder(), b.getStopOrder()))
                .map(TripStopResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
