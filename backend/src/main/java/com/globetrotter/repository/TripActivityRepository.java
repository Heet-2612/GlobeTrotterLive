package com.globetrotter.repository;

import com.globetrotter.entity.TripActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripActivityRepository extends JpaRepository<TripActivity, Long> {

    List<TripActivity> findByTripStopIdOrderByActivityOrderAsc(Long tripStopId);

    Optional<TripActivity> findByIdAndTripStopId(Long id, Long tripStopId);

    long countByTripStopId(Long tripStopId);
}
