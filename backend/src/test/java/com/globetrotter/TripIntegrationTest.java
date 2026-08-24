package com.globetrotter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globetrotter.dto.AuthResponse;
import com.globetrotter.dto.CreateTripRequest;
import com.globetrotter.dto.SignupRequest;
import com.globetrotter.dto.UpdateTripRequest;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TripIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripStopRepository tripStopRepository;

    @Autowired
    private TripActivityRepository tripActivityRepository;

    private String userAToken;
    private String userBToken;

    @BeforeEach
    void setUp() throws Exception {
        tripActivityRepository.deleteAll();
        tripStopRepository.deleteAll();
        tripRepository.deleteAll();
        userRepository.deleteAll();

        // Register User A
        SignupRequest signupA = new SignupRequest("User A", "usera@example.com", "Password123!");
        MvcResult resultA = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupA)))
                .andExpect(status().isCreated())
                .andReturn();
        AuthResponse authA = objectMapper.readValue(resultA.getResponse().getContentAsString(), AuthResponse.class);
        userAToken = authA.getToken();

        // Register User B
        SignupRequest signupB = new SignupRequest("User B", "userb@example.com", "Password123!");
        MvcResult resultB = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupB)))
                .andExpect(status().isCreated())
                .andReturn();
        AuthResponse authB = objectMapper.readValue(resultB.getResponse().getContentAsString(), AuthResponse.class);
        userBToken = authB.getToken();
    }

    @Test
    @DisplayName("1. Authenticated user can create a trip")
    void test1_AuthenticatedUserCanCreateTrip() throws Exception {
        CreateTripRequest request = new CreateTripRequest(
                "Euro Summer 2026",
                "Backpacking through Europe",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 15),
                "https://example.com/paris.jpg"
        );

        mockMvc.perform(post("/api/trips")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Euro Summer 2026")))
                .andExpect(jsonPath("$.description", is("Backpacking through Europe")))
                .andExpect(jsonPath("$.startDate", is("2026-06-01")))
                .andExpect(jsonPath("$.endDate", is("2026-06-15")))
                .andExpect(jsonPath("$.coverPhoto", is("https://example.com/paris.jpg")));

        assertEquals(1, tripRepository.count());
    }

    @Test
    @DisplayName("2. Unauthenticated user cannot create a trip")
    void test2_UnauthenticatedUserCannotCreateTrip() throws Exception {
        CreateTripRequest request = new CreateTripRequest(
                "Unauthorized Trip",
                "No token",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 10),
                null
        );

        mockMvc.perform(post("/api/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("3. Invalid trip name is rejected")
    void test3_InvalidTripNameIsRejected() throws Exception {
        CreateTripRequest request = new CreateTripRequest(
                "", // Blank name
                "Test description",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 10),
                null
        );

        mockMvc.perform(post("/api/trips")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("4. Missing dates are rejected")
    void test4_MissingDatesAreRejected() throws Exception {
        CreateTripRequest request = new CreateTripRequest(
                "Missing End Date",
                "Test description",
                LocalDate.of(2026, 8, 1),
                null, // missing end date
                null
        );

        mockMvc.perform(post("/api/trips")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("5. Start date after end date is rejected")
    void test5_StartDateAfterEndDateIsRejected() throws Exception {
        CreateTripRequest request = new CreateTripRequest(
                "Time Travel Trip",
                "Invalid dates",
                LocalDate.of(2026, 8, 15),
                LocalDate.of(2026, 8, 1), // start date after end date
                null
        );

        mockMvc.perform(post("/api/trips")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

