package com.globetrotter.repository;

import com.globetrotter.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("SELECT a FROM Activity a WHERE " +
           "(:cityId IS NULL OR a.city.id = :cityId) AND " +
           "(:search IS NULL OR :search = '' OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR LOWER(a.category) = LOWER(:category)) " +
           "ORDER BY a.name ASC")
    List<Activity> searchActivities(@Param("cityId") Long cityId,
                                    @Param("search") String search,
                                    @Param("category") String category);

    List<Activity> findByCityId(Long cityId);
}
