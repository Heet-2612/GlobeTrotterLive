package com.globetrotter.config;

import com.globetrotter.entity.Activity;
import com.globetrotter.entity.City;
import com.globetrotter.repository.ActivityRepository;
import com.globetrotter.repository.CityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CityDataInitializer implements CommandLineRunner {

    private final CityRepository cityRepository;
    private final ActivityRepository activityRepository;

    public CityDataInitializer(CityRepository cityRepository, ActivityRepository activityRepository) {
        this.cityRepository = cityRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    public void run(String... args) {
        if (cityRepository.count() == 0) {
            cityRepository.saveAll(List.of(
                new City(null, "Mumbai", "India", "Asia", 2.50, 85, "https://images.unsplash.com/photo-1570168007204-dfb528c6958f"),
                new City(null, "Goa", "India", "Asia", 2.00, 90, "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2"),
                new City(null, "Bangalore", "India", "Asia", 2.20, 80, "https://images.unsplash.com/photo-1596176530529-78163a4f7af2"),
                new City(null, "Delhi", "India", "Asia", 2.30, 88, "https://images.unsplash.com/photo-1587474260584-136574528ed5"),
                new City(null, "Jaipur", "India", "Asia", 1.80, 82, "https://images.unsplash.com/photo-1477587458883-47145ed94245"),
                new City(null, "Paris", "France", "Europe", 4.20, 98, "https://images.unsplash.com/photo-1502602898657-3e91760cbb34"),
                new City(null, "Rome", "Italy", "Europe", 3.80, 95, "https://images.unsplash.com/photo-1552832230-c0197dd311b5"),
                new City(null, "Tokyo", "Japan", "Asia", 4.50, 96, "https://images.unsplash.com/photo-1503899036084-c55cdd92da26")
            ));
        }

        if (activityRepository.count() == 0) {
            Map<String, City> cityMap = cityRepository.findAll().stream()
                    .collect(Collectors.toMap(City::getName, Function.identity()));

            City goa = cityMap.get("Goa");
            City mumbai = cityMap.get("Mumbai");
            City bangalore = cityMap.get("Bangalore");
            City paris = cityMap.get("Paris");
            City rome = cityMap.get("Rome");
            City tokyo = cityMap.get("Tokyo");

            if (goa != null) {
                activityRepository.saveAll(List.of(
                    new Activity(null, goa, "Baga Beach Sunset & Watersports", "Relax on golden sands and try parasailing.", "RELAXATION", 180, 2500.00, "INR", "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2"),
                    new Activity(null, goa, "Fort Aguada Exploration", "17th-century Portuguese lighthouse and fort overlooking the Arabian Sea.", "CULTURE", 120, 500.00, "INR", "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2"),
                    new Activity(null, goa, "Scuba Diving at Grande Island", "Underwater reef diving with experienced instructors.", "ADVENTURE", 240, 6000.00, "INR", "https://images.unsplash.com/photo-1544551763-46a013bb70d5"),
                    new Activity(null, goa, "Mandovi River Sunset Cruise", "Evening catamaran cruise with live music and traditional dance.", "NIGHTLIFE", 90, 1500.00, "INR", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e")
                ));
            }

            if (mumbai != null) {
                activityRepository.saveAll(List.of(
                    new Activity(null, mumbai, "Gateway of India & Taj Hotel Walk", "Iconic basalt arch monument erected during the British Raj.", "SIGHTSEEING", 90, 0.00, "INR", "https://images.unsplash.com/photo-1570168007204-dfb528c6958f"),
                    new Activity(null, mumbai, "Marine Drive Evening Promenade", "The Queen's Necklace C-shaped coastal boulevard.", "RELAXATION", 120, 0.00, "INR", "https://images.unsplash.com/photo-1570168007204-dfb528c6958f"),
                    new Activity(null, mumbai, "Elephanta Caves Ferry Tour", "UNESCO World Heritage rock-cut cave temples dedicated to Lord Shiva.", "CULTURE", 240, 1200.00, "INR", "https://images.unsplash.com/photo-1570168007204-dfb528c6958f")
                ));
            }

            if (bangalore != null) {
                activityRepository.saveAll(List.of(
                    new Activity(null, bangalore, "Bangalore Palace Tour", "Tudor-style royal palace featuring ornate wooden carvings and gardens.", "CULTURE", 120, 800.00, "INR", "https://images.unsplash.com/photo-1596176530529-78163a4f7af2"),
                    new Activity(null, bangalore, "Lalbagh Botanical Garden Stroll", "240-acre botanical garden with glass house inspired by London Crystal Palace.", "RELAXATION", 150, 300.00, "INR", "https://images.unsplash.com/photo-1596176530529-78163a4f7af2")
                ));
            }

            if (paris != null) {
                activityRepository.saveAll(List.of(
                    new Activity(null, paris, "Eiffel Tower Summit Access", "Panoramic views of Paris from the 276-meter summit observation deck.", "SIGHTSEEING", 150, 3000.00, "INR", "https://images.unsplash.com/photo-1502602898657-3e91760cbb34"),
                    new Activity(null, paris, "Louvre Museum Guided Tour", "Home of the Mona Lisa, Venus de Milo, and over 35,000 artworks.", "CULTURE", 180, 2200.00, "INR", "https://images.unsplash.com/photo-1499856871958-5b9627545d1a")
                ));
            }

            if (rome != null) {
                activityRepository.saveAll(List.of(
                    new Activity(null, rome, "Colosseum & Roman Forum Priority Tour", "Flavian Amphitheatre historic tour of gladiator battle arenas.", "CULTURE", 180, 2800.00, "INR", "https://images.unsplash.com/photo-1552832230-c0197dd311b5"),
                    new Activity(null, rome, "Trevi Fountain & Gelato Tasting", "Toss a coin into Rome's famous Baroque fountain and taste authentic gelato.", "FOOD", 90, 500.00, "INR", "https://images.unsplash.com/photo-1552832230-c0197dd311b5")
                ));
            }

            if (tokyo != null) {
                activityRepository.saveAll(List.of(
                    new Activity(null, tokyo, "Shibuya Crossing & Hachiko Statue", "World's busiest pedestrian scramble crossing.", "SIGHTSEEING", 60, 0.00, "INR", "https://images.unsplash.com/photo-1503899036084-c55cdd92da26"),
                    new Activity(null, tokyo, "Senso-ji Temple & Nakamise Shopping", "Tokyo's oldest Buddhist temple in historic Asakusa.", "CULTURE", 120, 0.00, "INR", "https://images.unsplash.com/photo-1503899036084-c55cdd92da26")
                ));
            }
        }
    }
}
