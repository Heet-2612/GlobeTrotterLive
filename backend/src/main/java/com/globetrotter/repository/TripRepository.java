package com.globetrotter.repository;

import com.globetrotter.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByUserIdOrderByStartDateAsc(Long userId);

    Optional<Trip> findByIdAndUserId(Long id, Long userId);

    Optional<Trip> findByShareToken(String shareToken);

    Optional<Trip> findByShareTokenAndIsPublicTrue(String shareToken);
}
