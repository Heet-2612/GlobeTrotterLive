package com.globetrotter.service;

import com.globetrotter.dto.CityResponse;
import com.globetrotter.entity.City;
import com.globetrotter.exception.ResourceNotFoundException;
import com.globetrotter.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional(readOnly = true)
    public List<CityResponse> searchCities(String search, String country, String region) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String cleanCountry = (country != null && !country.trim().isEmpty()) ? country.trim() : null;
        String cleanRegion = (region != null && !region.trim().isEmpty()) ? region.trim() : null;

        return cityRepository.searchCities(cleanSearch, cleanCountry, cleanRegion)
                .stream()
                .map(CityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CityResponse getCityById(Long cityId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + cityId));
        return CityResponse.fromEntity(city);
    }
}
