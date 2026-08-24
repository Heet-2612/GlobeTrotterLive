package com.globetrotter.repository;

import com.globetrotter.entity.TripStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripStopRepository extends JpaRepository<TripStop, Long> {

    List<TripStop> findByTripIdOrderByStopOrderAsc(Long tripId);

    Optional<TripStop> findByIdAndTripId(Long id, Long tripId);

    long countByTripId(Long tripId);
}
