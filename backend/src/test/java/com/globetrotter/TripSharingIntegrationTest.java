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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TripSharingIntegrationTest {

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

    @BeforeEach
    void setUp() throws Exception {
        tripActivityRepository.deleteAll();
        tripStopRepository.deleteAll();
        tripRepository.deleteAll();
        userRepository.deleteAll();
        activityRepository.deleteAll();
        cityRepository.deleteAll();

        // Seed City & Activity
        goa = cityRepository.save(new City(null, "Goa", "India", "Asia", 2.00, 90, "https://example.com/goa.jpg"));
        scubaGoa = activityRepository.save(new Activity(null, goa, "Scuba Diving", "Deep sea dive", "ADVENTURE", 240, 6000.00, "INR", null));

        // Register Users
        SignupRequest signupA = new SignupRequest("User A", "usera.share@example.com", "Password123!");
        MvcResult resA = mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupA))).andExpect(status().isCreated()).andReturn();
        userAToken = objectMapper.readValue(resA.getResponse().getContentAsString(), AuthResponse.class).getToken();

        SignupRequest signupB = new SignupRequest("User B", "userb.share@example.com", "Password123!");
        MvcResult resB = mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupB))).andExpect(status().isCreated()).andReturn();
        userBToken = objectMapper.readValue(resB.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Create Trip for User A
        CreateTripRequest tripReq = new CreateTripRequest("Goa Public Trip", "Sharable itinerary", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), null, new BigDecimal("500.00"));
        MvcResult tripRes = mockMvc.perform(post("/api/trips").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tripReq))).andExpect(status().isCreated()).andReturn();
        userATripId = objectMapper.readTree(tripRes.getResponse().getContentAsString()).get("id").asLong();

        // Add Stop & Activity
        CreateTripStopRequest stopReq = new CreateTripStopRequest(goa.getId(), LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 10), "Goa stop");
        MvcResult stopRes = mockMvc.perform(post("/api/trips/" + userATripId + "/stops").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(stopReq))).andExpect(status().isCreated()).andReturn();
        userAGoaStopId = objectMapper.readTree(stopRes.getResponse().getContentAsString()).get("id").asLong();

        CreateTripActivityRequest actReq = new CreateTripActivityRequest(scubaGoa.getId(), LocalDate.of(2026, 9, 6), LocalTime.of(10, 0), "Exciting dive", 60.00);
        mockMvc.perform(post("/api/trips/" + userATripId + "/stops/" + userAGoaStopId + "/activities").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(actReq))).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("1. New trip defaults to isPublic = false and shareToken = null")
    void test1_NewTripDefaultsToPrivate() throws Exception {
        mockMvc.perform(get("/api/trips/" + userATripId)
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPublic", is(false)))
                .andExpect(jsonPath("$.shareToken", nullValue()));
    }

    @Test
    @DisplayName("2. Enable public sharing generates UUID share token")
    void test2_EnablePublicSharingGeneratesToken() throws Exception {
        UpdateSharingRequest req = new UpdateSharingRequest(true);

        MvcResult result = mockMvc.perform(put("/api/trips/" + userATripId + "/sharing")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPublic", is(true)))
                .andExpect(jsonPath("$.shareToken", notNullValue()))
                .andExpect(jsonPath("$.publicUrl", startsWith("/shared/")))
                .andReturn();

        String shareToken = objectMapper.readTree(result.getResponse().getContentAsString()).get("shareToken").asText();
        assertFalse(shareToken.isBlank());
    }

    @Test
    @DisplayName("3. Retrieve sharing status for owner")
    void test3_RetrieveSharingStatus() throws Exception {
        UpdateSharingRequest req = new UpdateSharingRequest(true);
        mockMvc.perform(put("/api/trips/" + userATripId + "/sharing").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isOk());

        mockMvc.perform(get("/api/trips/" + userATripId + "/sharing")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPublic", is(true)))
                .andExpect(jsonPath("$.shareToken", notNullValue()));
    }

    @Test
    @DisplayName("4. Public user can view public itinerary without authentication")
    void test4_PublicUserCanViewPublicItinerary() throws Exception {
        // Enable sharing
        UpdateSharingRequest req = new UpdateSharingRequest(true);
        MvcResult shareRes = mockMvc.perform(put("/api/trips/" + userATripId + "/sharing").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isOk()).andReturn();
        String shareToken = objectMapper.readTree(shareRes.getResponse().getContentAsString()).get("shareToken").asText();

        // Perform GET without Authorization header!
        mockMvc.perform(get("/api/public/trips/" + shareToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Goa Public Trip")))
                .andExpect(jsonPath("$.creatorName", is("User A")))
                .andExpect(jsonPath("$.budget").value(500.0))
                .andExpect(jsonPath("$.stops", hasSize(1)))
                .andExpect(jsonPath("$.stops[0].city.name", is("Goa")))
                .andExpect(jsonPath("$.stops[0].activities", hasSize(1)))
                .andExpect(jsonPath("$.stops[0].activities[0].activityName", is("Scuba Diving")));
    }

    @Test
    @DisplayName("5. Viewing unshared/private trip returns 404 Not Found")
    void test5_ViewingPrivateTripReturns404() throws Exception {
        // Enable sharing to get a token, then disable
        UpdateSharingRequest enableReq = new UpdateSharingRequest(true);
        MvcResult shareRes = mockMvc.perform(put("/api/trips/" + userATripId + "/sharing").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(enableReq))).andExpect(status().isOk()).andReturn();
        String shareToken = objectMapper.readTree(shareRes.getResponse().getContentAsString()).get("shareToken").asText();

        UpdateSharingRequest disableReq = new UpdateSharingRequest(false);
        mockMvc.perform(put("/api/trips/" + userATripId + "/sharing").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(disableReq))).andExpect(status().isOk());

        // Try viewing with disabled token
        mockMvc.perform(get("/api/public/trips/" + shareToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("6. Non-existent share token returns 404 Not Found")
    void test6_NonExistentShareTokenReturns404() throws Exception {
        mockMvc.perform(get("/api/public/trips/invalid-share-token-12345"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("7. Logged-in User B can copy a public shared trip")
    void test7_UserCanCopyPublicTrip() throws Exception {
        // Enable sharing
        UpdateSharingRequest req = new UpdateSharingRequest(true);
        MvcResult shareRes = mockMvc.perform(put("/api/trips/" + userATripId + "/sharing").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isOk()).andReturn();
        String shareToken = objectMapper.readTree(shareRes.getResponse().getContentAsString()).get("shareToken").asText();

        // User B copies public trip
        MvcResult copyRes = mockMvc.perform(post("/api/public/trips/" + shareToken + "/copy")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Copy of Goa Public Trip")))
                .andExpect(jsonPath("$.isPublic", is(false)))
                .andReturn();

        Long clonedTripId = objectMapper.readTree(copyRes.getResponse().getContentAsString()).get("id").asLong();

        // Verify User B now has stops and activities copied!
        mockMvc.perform(get("/api/trips/" + clonedTripId + "/stops")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].city.name", is("Goa")));
    }

    @Test
    @DisplayName("8. User B cannot modify User A's sharing settings")
    void test8_UserBCannotModifyUserASharingSettings() throws Exception {
        UpdateSharingRequest req = new UpdateSharingRequest(true);

        mockMvc.perform(put("/api/trips/" + userATripId + "/sharing")
                        .header("Authorization", "Bearer " + userBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }
}

