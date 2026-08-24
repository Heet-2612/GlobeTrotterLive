package com.globetrotter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globetrotter.dto.*;
import com.globetrotter.entity.*;
import com.globetrotter.repository.*;
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
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ActivityAndTripActivityIntegrationTest {

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

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TripActivityRepository tripActivityRepository;

    private String userAToken;
    private String userBToken;
    private Long userATripId;
    private Long userAGoaStopId;
    private City goa;
    private City paris;
    private Activity scubaGoa;
    private Activity fortGoa;
    private Activity eiffelParis;

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
        paris = cityRepository.save(new City(null, "Paris", "France", "Europe", 4.20, 98, "https://example.com/paris.jpg"));

        // Seed Activities
        scubaGoa = activityRepository.save(new Activity(null, goa, "Scuba Diving", "Underwater reef dive", "ADVENTURE", 240, 60.00, "USD", null));
        fortGoa = activityRepository.save(new Activity(null, goa, "Fort Aguada", "Portuguese fort tour", "CULTURE", 120, 5.00, "USD", null));
        eiffelParis = activityRepository.save(new Activity(null, paris, "Eiffel Tower", "Summit observation", "SIGHTSEEING", 150, 30.00, "USD", null));

        // Register User A & B
        SignupRequest signupA = new SignupRequest("User A", "usera.act@example.com", "Password123!");
        MvcResult resA = mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupA))).andExpect(status().isCreated()).andReturn();
        userAToken = objectMapper.readValue(resA.getResponse().getContentAsString(), AuthResponse.class).getToken();

        SignupRequest signupB = new SignupRequest("User B", "userb.act@example.com", "Password123!");
        MvcResult resB = mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupB))).andExpect(status().isCreated()).andReturn();
        userBToken = objectMapper.readValue(resB.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // User A creates Trip (Sept 1 to Sept 30)
        CreateTripRequest tripReq = new CreateTripRequest("Goa Trip", "Beach Vacation", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), null);
        MvcResult tripRes = mockMvc.perform(post("/api/trips").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tripReq))).andExpect(status().isCreated()).andReturn();
        userATripId = objectMapper.readTree(tripRes.getResponse().getContentAsString()).get("id").asLong();

        // User A adds Goa Stop (Sept 5 to Sept 10)
        CreateTripStopRequest stopReq = new CreateTripStopRequest(goa.getId(), LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 10), "Goa stop");
        MvcResult stopRes = mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(stopReq))).andExpect(status().isCreated()).andReturn();
        userAGoaStopId = objectMapper.readTree(stopRes.getResponse().getContentAsString()).get("id").asLong();
    }

    // ==========================================
    // ACTIVITY TESTS
    // ==========================================

    @Test
    @DisplayName("1. Retrieve activities")
    void test1_RetrieveActivities() throws Exception {
        mockMvc.perform(get("/api/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("2. Search activities")
    void test2_SearchActivities() throws Exception {
        mockMvc.perform(get("/api/activities").param("search", "scuba"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Scuba Diving")));
    }

    @Test
    @DisplayName("3. Filter activities by city")
    void test3_FilterActivitiesByCity() throws Exception {
        mockMvc.perform(get("/api/activities").param("cityId", goa.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("4. Filter activities by category")
    void test4_FilterActivitiesByCategory() throws Exception {
        mockMvc.perform(get("/api/activities").param("category", "ADVENTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Scuba Diving")));
    }

    @Test
    @DisplayName("5. Retrieve activity by ID")
    void test5_RetrieveActivityById() throws Exception {
        mockMvc.perform(get("/api/activities/" + scubaGoa.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Scuba Diving")));
    }

    @Test
    @DisplayName("6. Unknown activity returns 404")
    void test6_UnknownActivityReturns404() throws Exception {
        mockMvc.perform(get("/api/activities/999999"))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // TRIP ACTIVITY TESTS
    // ==========================================

    @Test
    @DisplayName("7. Authenticated user can add an activity to their trip stop")
    void test7_UserCanAddActivityToTripStop() throws Exception {
        CreateTripActivityRequest req = new CreateTripActivityRequest(
                scubaGoa.getId(), LocalDate.of(2026, 9, 6), LocalTime.of(10, 0), "Morning dive", 55.00
        );

        mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.activity.name", is("Scuba Diving")))
                .andExpect(jsonPath("$.customCost", is(55.00)))
                .andExpect(jsonPath("$.activityOrder", is(1)));
    }

    @Test
    @DisplayName("8. User can retrieve activities for their stop")
    void test8_UserCanRetrieveActivitiesForStop() throws Exception {
        CreateTripActivityRequest req1 = new CreateTripActivityRequest(scubaGoa.getId(), LocalDate.of(2026, 9, 6), LocalTime.of(10, 0), null, null);
        CreateTripActivityRequest req2 = new CreateTripActivityRequest(fortGoa.getId(), LocalDate.of(2026, 9, 7), LocalTime.of(14, 0), null, null);

        mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req1))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req2))).andExpect(status().isCreated());

        mockMvc.perform(get("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].activity.name", is("Scuba Diving")))
                .andExpect(jsonPath("$[1].activity.name", is("Fort Aguada")));
    }

    @Test
    @DisplayName("9. User can update a TripActivity")
    void test9_UserCanUpdateTripActivity() throws Exception {
        CreateTripActivityRequest req = new CreateTripActivityRequest(scubaGoa.getId(), LocalDate.of(2026, 9, 6), LocalTime.of(10, 0), "Initial", null);
        MvcResult res = mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated()).andReturn();
        Long taId = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asLong();

        UpdateTripActivityRequest updateReq = new UpdateTripActivityRequest(LocalDate.of(2026, 9, 7), LocalTime.of(11, 30), "Updated time", 50.00);

        mockMvc.perform(put("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities/" + taId)
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduledDate", is("2026-09-07")))
                .andExpect(jsonPath("$.startTime", is("11:30:00")))
                .andExpect(jsonPath("$.customCost", is(50.00)))
                .andExpect(jsonPath("$.notes", is("Updated time")));
    }

    @Test
    @DisplayName("10. User can delete a TripActivity")
    void test10_UserCanDeleteTripActivity() throws Exception {
        CreateTripActivityRequest req = new CreateTripActivityRequest(scubaGoa.getId(), LocalDate.of(2026, 9, 6), LocalTime.of(10, 0), null, null);
        MvcResult res = mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated()).andReturn();
        Long taId = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities/" + taId)
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isNoContent());

        assertEquals(0, tripActivityRepository.countByTripStopId(userAGoaStopId));
    }

    @Test
    @DisplayName("11. User can reorder activities")
    void test11_UserCanReorderActivities() throws Exception {
        CreateTripActivityRequest req1 = new CreateTripActivityRequest(scubaGoa.getId(), LocalDate.of(2026, 9, 6), LocalTime.of(10, 0), null, null);
        CreateTripActivityRequest req2 = new CreateTripActivityRequest(fortGoa.getId(), LocalDate.of(2026, 9, 7), LocalTime.of(14, 0), null, null);

        MvcResult r1 = mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req1))).andExpect(status().isCreated()).andReturn();
        MvcResult r2 = mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req2))).andExpect(status().isCreated()).andReturn();

        Long id1 = objectMapper.readTree(r1.getResponse().getContentAsString()).get("id").asLong();
        Long id2 = objectMapper.readTree(r2.getResponse().getContentAsString()).get("id").asLong();

        ReorderTripActivitiesRequest reorderReq = new ReorderTripActivitiesRequest(List.of(id2, id1));

        mockMvc.perform(patch("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities/reorder")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reorderReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(id2.intValue())))
                .andExpect(jsonPath("$[0].activityOrder", is(1)))
                .andExpect(jsonPath("$[1].id", is(id1.intValue())))
                .andExpect(jsonPath("$[1].activityOrder", is(2)));
    }

    @Test
    @DisplayName("12. Activity must belong to the same city as the TripStop")
    void test12_ActivityMustBelongToSameCityAsStop() throws Exception {
        // Goa stop, trying to add Paris Eiffel Tower
        CreateTripActivityRequest req = new CreateTripActivityRequest(
                eiffelParis.getId(), LocalDate.of(2026, 9, 6), LocalTime.of(10, 0), null, null
        );

        mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("13. Activity scheduled date must be inside TripStop dates")
    void test13_ActivityScheduledDateMustBeInsideStopDates() throws Exception {
        // Goa stop is Sept 5 to Sept 10. Trying to schedule Sept 15.
        CreateTripActivityRequest req = new CreateTripActivityRequest(
                scubaGoa.getId(), LocalDate.of(2026, 9, 15), LocalTime.of(10, 0), null, null
        );

        mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("14. Negative custom cost is rejected")
    void test14_NegativeCustomCostIsRejected() throws Exception {
        CreateTripActivityRequest req = new CreateTripActivityRequest(
                scubaGoa.getId(), LocalDate.of(2026, 9, 6), LocalTime.of(10, 0), null, -10.00
        );

        mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("15. User cannot access another user's TripActivity")
    void test15_UserCannotAccessAnotherUsersTripActivity() throws Exception {
        mockMvc.perform(get("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("16. User cannot modify another user's TripActivity")
    void test16_UserCannotModifyAnotherUsersTripActivity() throws Exception {
        CreateTripActivityRequest req = new CreateTripActivityRequest(scubaGoa.getId(), LocalDate.of(2026, 9, 6), LocalTime.of(10, 0), null, null);
        MvcResult res = mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated()).andReturn();
        Long taId = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asLong();

        UpdateTripActivityRequest updateReq = new UpdateTripActivityRequest(LocalDate.of(2026, 9, 7), LocalTime.of(11, 0), "Hacked", 0.00);

        mockMvc.perform(put("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities/" + taId)
                        .header("Authorization", "Bearer " + userBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("17. User cannot delete another user's TripActivity")
    void test17_UserCannotDeleteAnotherUsersTripActivity() throws Exception {
        CreateTripActivityRequest req = new CreateTripActivityRequest(scubaGoa.getId(), LocalDate.of(2026, 9, 6), LocalTime.of(10, 0), null, null);
        MvcResult res = mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated()).andReturn();
        Long taId = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities/" + taId)
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isNotFound());
    }
}
