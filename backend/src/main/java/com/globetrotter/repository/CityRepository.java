package com.globetrotter.repository;

import com.globetrotter.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @Query("SELECT c FROM City c WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.country) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.region) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:country IS NULL OR :country = '' OR LOWER(c.country) = LOWER(:country)) AND " +
           "(:region IS NULL OR :region = '' OR LOWER(c.region) = LOWER(:region)) " +
           "ORDER BY c.popularity DESC, c.name ASC")
    List<City> searchCities(@Param("search") String search,
                             @Param("country") String country,
                             @Param("region") String region);
}
