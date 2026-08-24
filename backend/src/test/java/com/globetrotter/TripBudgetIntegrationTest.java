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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TripBudgetIntegrationTest {

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
    private Activity scubaGoa;
    private Activity fortGoa;

    @BeforeEach
    void setUp() throws Exception {
        tripActivityRepository.deleteAll();
        tripStopRepository.deleteAll();
        tripRepository.deleteAll();
        userRepository.deleteAll();
        activityRepository.deleteAll();
        cityRepository.deleteAll();

        // Seed City & Activities
        goa = cityRepository.save(new City(null, "Goa", "India", "Asia", 2.00, 90, "https://example.com/goa.jpg"));
        scubaGoa = activityRepository.save(new Activity(null, goa, "Scuba Diving", "Deep sea dive", "ADVENTURE", 240, 6000.00, "INR", null));
        fortGoa = activityRepository.save(new Activity(null, goa, "Fort Aguada", "Portuguese fort tour", "CULTURE", 120, 1000.00, "INR", null));

        // Register Users
        SignupRequest signupA = new SignupRequest("User A", "usera.budget@example.com", "Password123!");
        MvcResult resA = mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupA))).andExpect(status().isCreated()).andReturn();
        userAToken = objectMapper.readValue(resA.getResponse().getContentAsString(), AuthResponse.class).getToken();

        SignupRequest signupB = new SignupRequest("User B", "userb.budget@example.com", "Password123!");
        MvcResult resB = mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupB))).andExpect(status().isCreated()).andReturn();
        userBToken = objectMapper.readValue(resB.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Create Trip for User A (with initial budget 500.00)
        CreateTripRequest tripReq = new CreateTripRequest("Goa Trip", "Beach Vacation", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), null, new BigDecimal("500.00"));
        MvcResult tripRes = mockMvc.perform(post("/api/trips").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tripReq))).andExpect(status().isCreated()).andReturn();
        userATripId = objectMapper.readTree(tripRes.getResponse().getContentAsString()).get("id").asLong();

        // Add Stop for Goa
        CreateTripStopRequest stopReq = new CreateTripStopRequest(goa.getId(), LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 10), "Goa stop");
        MvcResult stopRes = mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(stopReq))).andExpect(status().isCreated()).andReturn();
        userAGoaStopId = objectMapper.readTree(stopRes.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    @DisplayName("1. Create trip with budget included in response")
    void test1_CreateTripWithBudget() throws Exception {
        CreateTripRequest tripReq = new CreateTripRequest("Budgeted Trip", "With 1000 budget", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 10), null, new BigDecimal("1000.00"));
        mockMvc.perform(post("/api/trips")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.budget").value(1000.00));

        
    }

    @Test
    @DisplayName("2. Retrieve budget summary for configured trip")
    void test2_RetrieveBudgetSummary() throws Exception {
        mockMvc.perform(get("/api/trips/" + userATripId + "/budget")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId", is(userATripId.intValue())))
                .andExpect(jsonPath("$.budget").value(500.00))
                .andExpect(jsonPath("$.totalActivityCost").value(0))
                .andExpect(jsonPath("$.remainingBudget").value(0.0))
                .andExpect(jsonPath("$.budgetExceeded", is(false)));
    }

    @Test
    @DisplayName("3. Update trip budget via PUT /api/trips/{tripId}/budget")
    void test3_UpdateTripBudget() throws Exception {
        SetBudgetRequest setBudget = new SetBudgetRequest(new BigDecimal("750.00"));

        mockMvc.perform(put("/api/trips/" + userATripId + "/budget")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(setBudget)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.budget").value(750.00))
                .andExpect(jsonPath("$.remainingBudget").value(0.0));
    }

    @Test
    @DisplayName("4. Calculate total activity costs dynamically")
    void test4_CalculateTotalActivityCostsDynamically() throws Exception {
        CreateTripActivityRequest req1 = new CreateTripActivityRequest(scubaGoa.getId(), LocalDate.of(2026, 9, 6), LocalTime.of(10, 0), null, 55.00);
        mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req1))).andExpect(status().isCreated());

        CreateTripActivityRequest req2 = new CreateTripActivityRequest(fortGoa.getId(), LocalDate.of(2026, 9, 7), LocalTime.of(14, 0), null, null);
        mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req2))).andExpect(status().isCreated());

        mockMvc.perform(get("/api/trips/" + userATripId + "/budget")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActivityCost").value(1055.00))
                .andExpect(jsonPath("$.remainingBudget").value(0.0))
                .andExpect(jsonPath("$.budgetUsedPercentage").value(13.0));
    }

    @Test
    @DisplayName("5. Budget exceeded flag triggers when total exceeds budget")
    void test5_BudgetExceededFlagTriggers() throws Exception {
        SetBudgetRequest setBudget = new SetBudgetRequest(new BigDecimal("50.00"));
        mockMvc.perform(put("/api/trips/" + userATripId + "/budget").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(setBudget))).andExpect(status().isOk());

        CreateTripActivityRequest req = new CreateTripActivityRequest(scubaGoa.getId(), LocalDate.of(2026, 9, 6), LocalTime.of(10, 0), null, 60.00);
        mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated());

        mockMvc.perform(get("/api/trips/" + userATripId + "/budget")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActivityCost").value(60.00))
                .andExpect(jsonPath("$.remainingBudget").value(-1000.00))
                .andExpect(jsonPath("$.budgetExceeded", is(true)));
    }

    @Test
    @DisplayName("6. Category breakdown calculation")
    void test6_CategoryBreakdownCalculation() throws Exception {
        CreateTripActivityRequest req1 = new CreateTripActivityRequest(scubaGoa.getId(), LocalDate.of(2026, 9, 6), LocalTime.of(10, 0), null, 60.00);
        CreateTripActivityRequest req2 = new CreateTripActivityRequest(fortGoa.getId(), LocalDate.of(2026, 9, 7), LocalTime.of(14, 0), null, 10.00);

        mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req1))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req2))).andExpect(status().isCreated());

        mockMvc.perform(get("/api/trips/" + userATripId + "/budget")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryBreakdown", hasSize(2)))
                .andExpect(jsonPath("$.categoryBreakdown[0].category", is("ADVENTURE")))
                .andExpect(jsonPath("$.categoryBreakdown[0].cost").value(60.00))
                .andExpect(jsonPath("$.categoryBreakdown[1].category", is("CULTURE")))
                .andExpect(jsonPath("$.categoryBreakdown[1].cost").value(10.0));
    }

    @Test
    @DisplayName("7. Reject negative budget")
    void test7_RejectNegativeBudget() throws Exception {
        SetBudgetRequest setBudget = new SetBudgetRequest(new BigDecimal("-100.00"));

        mockMvc.perform(put("/api/trips/" + userATripId + "/budget")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(setBudget)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("8. User B cannot view User A's trip budget")
    void test8_UserBCannotViewUserATripBudget() throws Exception {
        mockMvc.perform(get("/api/trips/" + userATripId + "/budget")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("9. User B cannot set User A's trip budget")
    void test9_UserBCannotSetUserATripBudget() throws Exception {
        SetBudgetRequest setBudget = new SetBudgetRequest(new BigDecimal("500.00"));

        mockMvc.perform(put("/api/trips/" + userATripId + "/budget")
                        .header("Authorization", "Bearer " + userBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(setBudget)))
                .andExpect(status().isNotFound());
    }
}


