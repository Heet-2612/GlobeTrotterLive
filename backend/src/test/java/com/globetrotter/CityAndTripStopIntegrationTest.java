package com.globetrotter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globetrotter.dto.*;
import com.globetrotter.entity.City;
import com.globetrotter.repository.ActivityRepository;
import com.globetrotter.repository.CityRepository;
import com.globetrotter.repository.TripActivityRepository;
import com.globetrotter.repository.TripRepository;
import com.globetrotter.repository.TripStopRepository;
import com.globetrotter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CityAndTripStopIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private TripStopRepository tripStopRepository;

    private String userAToken;
    private String userBToken;
    private Long userATripId;
    private City goa;
    private City mumbai;
    private City bangalore;

    @Autowired
    private TripActivityRepository tripActivityRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @BeforeEach
    void setUp() throws Exception {
        tripActivityRepository.deleteAll();
        tripStopRepository.deleteAll();
        tripRepository.deleteAll();
        userRepository.deleteAll();
        activityRepository.deleteAll();
        cityRepository.deleteAll();

        // Seed Cities
        goa = cityRepository.save(new City(null, "Goa", "India", "Asia", 2.00, 90, "https://example.com/goa.jpg"));
        mumbai = cityRepository.save(new City(null, "Mumbai", "India", "Asia", 2.50, 85, "https://example.com/mumbai.jpg"));
        bangalore = cityRepository.save(new City(null, "Bangalore", "India", "Asia", 2.20, 80, "https://example.com/bangalore.jpg"));

        // Register User A
        SignupRequest signupA = new SignupRequest("User A", "usera.stop@example.com", "Password123!");
        MvcResult resultA = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupA)))
                .andExpect(status().isCreated())
                .andReturn();
        AuthResponse authA = objectMapper.readValue(resultA.getResponse().getContentAsString(), AuthResponse.class);
        userAToken = authA.getToken();

        // Register User B
        SignupRequest signupB = new SignupRequest("User B", "userb.stop@example.com", "Password123!");
        MvcResult resultB = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupB)))
                .andExpect(status().isCreated())
                .andReturn();
        AuthResponse authB = objectMapper.readValue(resultB.getResponse().getContentAsString(), AuthResponse.class);
        userBToken = authB.getToken();

        // Create User A Trip (Sept 1 - Sept 30)
        CreateTripRequest tripRequest = new CreateTripRequest(
                "India Tour 2026", "Grand India Trip",
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), null
        );
        MvcResult tripResult = mockMvc.perform(post("/api/trips")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        userATripId = objectMapper.readTree(tripResult.getResponse().getContentAsString()).get("id").asLong();
    }

    // ==========================================
    // CITIES TESTS
    // ==========================================

    @Test
    @DisplayName("1. List/search cities")
    void test1_ListAndSearchCities() throws Exception {
        mockMvc.perform(get("/api/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("2. Search is case-insensitive")
    void test2_SearchIsCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/cities").param("search", "gOa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Goa")));
    }

    @Test
    @DisplayName("3. Retrieve city by ID")
    void test3_RetrieveCityById() throws Exception {
        mockMvc.perform(get("/api/cities/" + goa.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(goa.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Goa")));
    }

    @Test
    @DisplayName("4. Unknown city returns 404")
    void test4_UnknownCityReturns404() throws Exception {
        mockMvc.perform(get("/api/cities/99999"))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // TRIP STOPS TESTS
    // ==========================================

    @Test
    @DisplayName("5. Authenticated user can add a city to their trip")
    void test5_UserCanAddCityToTrip() throws Exception {
        CreateTripStopRequest request = new CreateTripStopRequest(
                goa.getId(), LocalDate.of(2026, 9, 2), LocalDate.of(2026, 9, 5), "Beach stay"
        );

        mockMvc.perform(post("/api/trips/" + userATripId + "/stops")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.stopOrder", is(1)))
                .andExpect(jsonPath("$.city.name", is("Goa")));
    }

    @Test
    @DisplayName("6. User can retrieve their trip stops")
    void test6_UserCanRetrieveTripStops() throws Exception {
        CreateTripStopRequest stop1 = new CreateTripStopRequest(mumbai.getId(), LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 4), null);
        CreateTripStopRequest stop2 = new CreateTripStopRequest(goa.getId(), LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 10), null);

        mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(stop1))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(stop2))).andExpect(status().isCreated());

        mockMvc.perform(get("/api/trips/" + userATripId + "/stops")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].city.name", is("Mumbai")))
                .andExpect(jsonPath("$[1].city.name", is("Goa")));
    }

    @Test
    @DisplayName("7. User can update a stop")
    void test7_UserCanUpdateStop() throws Exception {
        CreateTripStopRequest stop = new CreateTripStopRequest(goa.getId(), LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 10), "Initial");
        MvcResult res = mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(stop))).andExpect(status().isCreated()).andReturn();
        Long stopId = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asLong();

        UpdateTripStopRequest update = new UpdateTripStopRequest(LocalDate.of(2026, 9, 6), LocalDate.of(2026, 9, 12), "Extended stay");

        mockMvc.perform(put("/api/trips/" + userATripId + "/stops/" + stopId)
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate", is("2026-09-06")))
                .andExpect(jsonPath("$.endDate", is("2026-09-12")))
                .andExpect(jsonPath("$.notes", is("Extended stay")));
    }

    @Test
    @DisplayName("8. User can delete a stop")
    void test8_UserCanDeleteStop() throws Exception {
        CreateTripStopRequest stop = new CreateTripStopRequest(goa.getId(), LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 10), null);
        MvcResult res = mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(stop))).andExpect(status().isCreated()).andReturn();
        Long stopId = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/trips/" + userATripId + "/stops/" + stopId)
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isNoContent());

        assertEquals(0, tripStopRepository.countByTripId(userATripId));
    }

    @Test
    @DisplayName("9. User can reorder stops")
    void test9_UserCanReorderStops() throws Exception {
        CreateTripStopRequest s1 = new CreateTripStopRequest(mumbai.getId(), LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 3), null);
        CreateTripStopRequest s2 = new CreateTripStopRequest(goa.getId(), LocalDate.of(2026, 9, 4), LocalDate.of(2026, 9, 7), null);
        CreateTripStopRequest s3 = new CreateTripStopRequest(bangalore.getId(), LocalDate.of(2026, 9, 8), LocalDate.of(2026, 9, 10), null);

        MvcResult r1 = mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(s1))).andExpect(status().isCreated()).andReturn();
        MvcResult r2 = mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(s2))).andExpect(status().isCreated()).andReturn();
        MvcResult r3 = mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(s3))).andExpect(status().isCreated()).andReturn();

        Long id1 = objectMapper.readTree(r1.getResponse().getContentAsString()).get("id").asLong();
        Long id2 = objectMapper.readTree(r2.getResponse().getContentAsString()).get("id").asLong();
        Long id3 = objectMapper.readTree(r3.getResponse().getContentAsString()).get("id").asLong();

        // Reorder: 3 (Bangalore), 1 (Mumbai), 2 (Goa)
        ReorderStopsRequest reorder = new ReorderStopsRequest(List.of(id3, id1, id2));

        mockMvc.perform(patch("/api/trips/" + userATripId + "/stops/reorder")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reorder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(id3.intValue())))
                .andExpect(jsonPath("$[0].stopOrder", is(1)))
                .andExpect(jsonPath("$[1].id", is(id1.intValue())))
                .andExpect(jsonPath("$[1].stopOrder", is(2)))
                .andExpect(jsonPath("$[2].id", is(id2.intValue())))
                .andExpect(jsonPath("$[2].stopOrder", is(3)));
    }

    @Test
    @DisplayName("10. Invalid stop dates are rejected (start > end)")
    void test10_InvalidStopDatesAreRejected() throws Exception {
        CreateTripStopRequest request = new CreateTripStopRequest(
                goa.getId(), LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 5), null
        );

        mockMvc.perform(post("/api/trips/" + userATripId + "/stops")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("11. Stop outside trip date range is rejected")
    void test11_StopOutsideTripDateRangeIsRejected() throws Exception {
        // Parent trip is Sept 1 - Sept 30
        CreateTripStopRequest request = new CreateTripStopRequest(
                goa.getId(), LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 5), null
        );

        mockMvc.perform(post("/api/trips/" + userATripId + "/stops")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("12. User cannot access another user's trip stops")
    void test12_UserCannotAccessAnotherUsersTripStops() throws Exception {
        mockMvc.perform(get("/api/trips/" + userATripId + "/stops")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("13. User cannot modify another user's trip stop")
    void test13_UserCannotModifyAnotherUsersTripStop() throws Exception {
        CreateTripStopRequest stop = new CreateTripStopRequest(goa.getId(), LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 10), null);
        MvcResult res = mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(stop))).andExpect(status().isCreated()).andReturn();
        Long stopId = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asLong();

        UpdateTripStopRequest update = new UpdateTripStopRequest(LocalDate.of(2026, 9, 6), LocalDate.of(2026, 9, 11), "Hacked");

        mockMvc.perform(put("/api/trips/" + userATripId + "/stops/" + stopId)
                        .header("Authorization", "Bearer " + userBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("14. User cannot delete another user's trip stop")
    void test14_UserCannotDeleteAnotherUsersTripStop() throws Exception {
        CreateTripStopRequest stop = new CreateTripStopRequest(goa.getId(), LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 10), null);
        MvcResult res = mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(stop))).andExpect(status().isCreated()).andReturn();
        Long stopId = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/trips/" + userATripId + "/stops/" + stopId)
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("15. Stop order remains valid after reordering")
    void test15_StopOrderRemainsValidAfterReordering() throws Exception {
        CreateTripStopRequest s1 = new CreateTripStopRequest(mumbai.getId(), LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 3), null);
        CreateTripStopRequest s2 = new CreateTripStopRequest(goa.getId(), LocalDate.of(2026, 9, 4), LocalDate.of(2026, 9, 7), null);

        MvcResult r1 = mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(s1))).andExpect(status().isCreated()).andReturn();
        MvcResult r2 = mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(s2))).andExpect(status().isCreated()).andReturn();

        Long id1 = objectMapper.readTree(r1.getResponse().getContentAsString()).get("id").asLong();
        Long id2 = objectMapper.readTree(r2.getResponse().getContentAsString()).get("id").asLong();

        // Swap order: id2, id1
        ReorderStopsRequest reorder = new ReorderStopsRequest(List.of(id2, id1));

        mockMvc.perform(patch("/api/trips/" + userATripId + "/stops/reorder")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reorder)))
                .andExpect(status().isOk());

        // Fetch stops and verify sequential 1, 2 ordering
        mockMvc.perform(get("/api/trips/" + userATripId + "/stops")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stopOrder", is(1)))
                .andExpect(jsonPath("$[0].id", is(id2.intValue())))
                .andExpect(jsonPath("$[1].stopOrder", is(2)))
                .andExpect(jsonPath("$[1].id", is(id1.intValue())));
    }
}
