package com.globetrotter.service;

import com.globetrotter.dto.ActivityResponse;
import com.globetrotter.entity.Activity;
import com.globetrotter.exception.ResourceNotFoundException;
import com.globetrotter.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> searchActivities(Long cityId, String search, String category) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String cleanCategory = (category != null && !category.trim().isEmpty()) ? category.trim() : null;

        return activityRepository.searchActivities(cityId, cleanSearch, cleanCategory)
                .stream()
                .map(ActivityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ActivityResponse getActivityById(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id: " + activityId));
        return ActivityResponse.fromEntity(activity);
    }
}
