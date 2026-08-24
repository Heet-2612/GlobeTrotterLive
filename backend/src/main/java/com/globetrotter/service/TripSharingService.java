package com.globetrotter.service;

import com.globetrotter.dto.*;
import com.globetrotter.entity.*;
import com.globetrotter.exception.ResourceNotFoundException;
import com.globetrotter.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TripSharingService {

    private final TripRepository tripRepository;
    private final TripStopRepository tripStopRepository;
    private final TripActivityRepository tripActivityRepository;

    public TripSharingService(TripRepository tripRepository, TripStopRepository tripStopRepository, TripActivityRepository tripActivityRepository) {
        this.tripRepository = tripRepository;
        this.tripStopRepository = tripStopRepository;
        this.tripActivityRepository = tripActivityRepository;
    }

    @Transactional
    public TripSharingResponse updateSharing(Long tripId, UpdateSharingRequest request, User currentUser) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        if (Boolean.TRUE.equals(request.getIsPublic())) {
            trip.setIsPublic(true);
            if (trip.getShareToken() == null || trip.getShareToken().trim().isEmpty()) {
                trip.setShareToken(UUID.randomUUID().toString());
            }
        } else {
            trip.setIsPublic(false);
        }

        Trip saved = tripRepository.save(trip);
        String publicUrl = saved.getShareToken() != null ? "/shared/" + saved.getShareToken() : null;
        return new TripSharingResponse(saved.getId(), saved.getIsPublic(), saved.getShareToken(), publicUrl);
    }

    @Transactional(readOnly = true)
    public TripSharingResponse getSharingStatus(Long tripId, User currentUser) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        String publicUrl = trip.getShareToken() != null ? "/shared/" + trip.getShareToken() : null;
        return new TripSharingResponse(trip.getId(), trip.getIsPublic(), trip.getShareToken(), publicUrl);
    }

    @Transactional(readOnly = true)
    public PublicTripItineraryResponse getPublicTripItinerary(String shareToken) {
        Trip trip = tripRepository.findByShareTokenAndIsPublicTrue(shareToken)
                .orElseThrow(() -> new ResourceNotFoundException("Public trip not found with share token: " + shareToken));

        List<TripStop> stops = tripStopRepository.findByTripIdOrderByStopOrderAsc(trip.getId());

        List<PublicTripStopResponse> publicStops = new ArrayList<>();
        for (TripStop stop : stops) {
            List<TripActivity> activities = tripActivityRepository.findByTripStopIdOrderByActivityOrderAsc(stop.getId());

            List<PublicTripActivityResponse> publicActivities = activities.stream()
                    .map(ta -> new PublicTripActivityResponse(
                            ta.getId(),
                            ta.getActivity() != null ? ta.getActivity().getName() : null,
                            ta.getActivity() != null ? ta.getActivity().getCategory() : null,
                            ta.getActivity() != null ? ta.getActivity().getDescription() : null,
                            ta.getActivity() != null ? ta.getActivity().getEstimatedDurationMinutes() : null,
                            ta.getCustomCost() != null ? ta.getCustomCost() : (ta.getActivity() != null ? ta.getActivity().getEstimatedCost() : 0.0),
                            ta.getScheduledDate(),
                            ta.getStartTime(),
                            ta.getNotes(),
                            ta.getActivityOrder()
                    ))
                    .collect(Collectors.toList());

            publicStops.add(new PublicTripStopResponse(
                    stop.getId(),
                    CityResponse.fromEntity(stop.getCity()),
                    stop.getStartDate(),
                    stop.getEndDate(),
                    stop.getStopOrder(),
                    stop.getNotes(),
                    publicActivities
            ));
        }

        String creatorName = trip.getUser() != null ? trip.getUser().getName() : "Anonymous";

        return new PublicTripItineraryResponse(
                trip.getId(),
                trip.getShareToken(),
                trip.getName(),
                trip.getDescription(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getCoverPhoto(),
                creatorName,
                trip.getBudget(),
                publicStops
        );
    }

    @Transactional
    public TripResponse copyPublicTrip(String shareToken, User currentUser) {
        Trip original = tripRepository.findByShareTokenAndIsPublicTrue(shareToken)
                .orElseThrow(() -> new ResourceNotFoundException("Public trip not found with share token: " + shareToken));

        Trip clonedTrip = Trip.builder()
                .user(currentUser)
                .name("Copy of " + original.getName())
                .description(original.getDescription())
                .startDate(original.getStartDate())
                .endDate(original.getEndDate())
                .coverPhoto(original.getCoverPhoto())
                .budget(original.getBudget())
                .isPublic(false)
                .build();

        Trip savedTrip = tripRepository.save(clonedTrip);

        List<TripStop> originalStops = tripStopRepository.findByTripIdOrderByStopOrderAsc(original.getId());
        for (TripStop origStop : originalStops) {
            TripStop clonedStop = TripStop.builder()
                    .trip(savedTrip)
                    .city(origStop.getCity())
                    .startDate(origStop.getStartDate())
                    .endDate(origStop.getEndDate())
                    .stopOrder(origStop.getStopOrder())
                    .notes(origStop.getNotes())
                    .build();

            TripStop savedStop = tripStopRepository.save(clonedStop);

            List<TripActivity> origActivities = tripActivityRepository.findByTripStopIdOrderByActivityOrderAsc(origStop.getId());
            for (TripActivity origTa : origActivities) {
                TripActivity clonedTa = TripActivity.builder()
                        .tripStop(savedStop)
                        .activity(origTa.getActivity())
                        .scheduledDate(origTa.getScheduledDate())
                        .startTime(origTa.getStartTime())
                        .notes(origTa.getNotes())
                        .customCost(origTa.getCustomCost())
                        .activityOrder(origTa.getActivityOrder())
                        .build();

                tripActivityRepository.save(clonedTa);
            }
        }

        return TripResponse.fromEntity(savedTrip);
    }
}
