package com.globetrotter.controller;

import com.globetrotter.dto.CityResponse;
import com.globetrotter.service.CityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<List<CityResponse>> searchCities(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String region) {
        List<CityResponse> response = cityService.searchCities(search, country, region);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cityId}")
    public ResponseEntity<CityResponse> getCityById(@PathVariable Long cityId) {
        CityResponse response = cityService.getCityById(cityId);
        return ResponseEntity.ok(response);
    }
}
