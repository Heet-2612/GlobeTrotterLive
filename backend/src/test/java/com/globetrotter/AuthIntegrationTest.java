package com.globetrotter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globetrotter.dto.ForgotPasswordRequest;
import com.globetrotter.dto.LoginRequest;
import com.globetrotter.dto.ResetPasswordRequest;
import com.globetrotter.dto.SignupRequest;
import com.globetrotter.entity.PasswordResetToken;
import com.globetrotter.entity.User;
import com.globetrotter.repository.PasswordResetTokenRepository;
import com.globetrotter.repository.UserRepository;
import com.globetrotter.security.PasswordResetTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.globetrotter.repository.TripActivityRepository tripActivityRepository;

    @Autowired
    private com.globetrotter.repository.TripStopRepository tripStopRepository;

    @Autowired
    private com.globetrotter.repository.TripRepository tripRepository;

    @BeforeEach
    void setUp() {
        passwordResetTokenRepository.deleteAll();
        tripActivityRepository.deleteAll();
        tripStopRepository.deleteAll();
        tripRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void test1_SuccessfulSignup() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .name("Aditya Test")
                .email("aditya@example.com")
                .password("Password123!")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.name").value("Aditya Test"))
                .andExpect(jsonPath("$.user.email").value("aditya@example.com"))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist());
    }

    @Test
    void test2_DuplicateEmailSignup_FailsWithBadRequest() throws Exception {
        SignupRequest request1 = SignupRequest.builder()
                .name("First User")
                .email("duplicate@example.com")
                .password("Password123!")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        SignupRequest request2 = SignupRequest.builder()
                .name("Second User")
                .email("duplicate@example.com")
                .password("Password123!")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is already registered: duplicate@example.com"));
    }

    @Test
    void test3_SuccessfulLogin() throws Exception {
        SignupRequest signup = SignupRequest.builder()
                .name("Login User")
                .email("login@example.com")
                .password("Password123!")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        LoginRequest login = LoginRequest.builder()
                .email("login@example.com")
                .password("Password123!")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value("login@example.com"));
    }

    @Test
    void test4_InvalidLogin_FailsWithUnauthorized() throws Exception {
        LoginRequest login = LoginRequest.builder()
                .email("nonexistent@example.com")
                .password("WrongPassword123!")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void test5_PasswordIsStoredHashed() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .name("Hash User")
                .email("hash@example.com")
                .password("SecretPassword123!")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail("hash@example.com").orElseThrow();

        assertNotEquals("SecretPassword123!", user.getPasswordHash());
        assertTrue(passwordEncoder.matches("SecretPassword123!", user.getPasswordHash()));
    }

    @Test
    void test6_GetMe_WithValidToken_ReturnsUserProfile() throws Exception {
        SignupRequest signup = SignupRequest.builder()
                .name("Me User")
                .email("me@example.com")
                .password("Password123!")
                .build();

        MvcResult signupResult = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = signupResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseJson).get("token").asText();

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("me@example.com"))
                .andExpect(jsonPath("$.name").value("Me User"));
    }

    @Test
    void test7_GetMe_WithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test8_PasswordValidation_RejectsWeakPassword() throws Exception {
        SignupRequest weakPasswordRequest = SignupRequest.builder()
                .name("Weak User")
                .email("weak@example.com")
                .password("simple")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(weakPasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Validation failed")));
    }

    @Test
    void test9_ForgotPassword_ExistingUser_GeneratesTokenHashAndReturnsGenericMessage() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Forgot User")
                .email("forgot@example.com")
                .passwordHash(passwordEncoder.encode("OldPassword123!"))
                .build());

        ForgotPasswordRequest forgotReq = new ForgotPasswordRequest("forgot@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("If an account exists for this email, password reset instructions have been sent."));

        assertEquals(1, passwordResetTokenRepository.count());
        PasswordResetToken resetToken = passwordResetTokenRepository.findAll().get(0);
        assertEquals(user.getId(), resetToken.getUser().getId());
        assertNotNull(resetToken.getTokenHash());
        assertNull(resetToken.getUsedAt());
        assertTrue(resetToken.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void test10_ForgotPassword_UnknownUser_ReturnsSameGenericMessage() throws Exception {
        ForgotPasswordRequest forgotReq = new ForgotPasswordRequest("nonexistent@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("If an account exists for this email, password reset instructions have been sent."));

        assertEquals(0, passwordResetTokenRepository.count());
    }

    @Test
    void test11_ResetPassword_ValidToken_SuccessfullyUpdatesPasswordAndInvalidatesOld() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Reset Flow User")
                .email("flow@example.com")
                .passwordHash(passwordEncoder.encode("OldPassword123!"))
                .build());

        String rawToken = PasswordResetTokenUtil.generateRawToken();
        String tokenHash = PasswordResetTokenUtil.hashToken(rawToken);

        passwordResetTokenRepository.save(PasswordResetToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                .build());

        ResetPasswordRequest resetReq = ResetPasswordRequest.builder()
                .token(rawToken)
                .newPassword("BrandNewPassword123!")
                .build();

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Your password has been reset successfully."));

        // Verify old password fails
        LoginRequest oldLogin = LoginRequest.builder().email("flow@example.com").password("OldPassword123!").build();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oldLogin)))
                .andExpect(status().isUnauthorized());

        // Verify new password works
        LoginRequest newLogin = LoginRequest.builder().email("flow@example.com").password("BrandNewPassword123!").build();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        // Verify token is marked used
        PasswordResetToken updatedToken = passwordResetTokenRepository.findByTokenHash(tokenHash).orElseThrow();
        assertNotNull(updatedToken.getUsedAt());
    }

    @Test
    void test12_ResetPassword_ReuseToken_Fails() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Reuse User")
                .email("reuse@example.com")
                .passwordHash(passwordEncoder.encode("OldPassword123!"))
                .build());

        String rawToken = PasswordResetTokenUtil.generateRawToken();
        String tokenHash = PasswordResetTokenUtil.hashToken(rawToken);

        passwordResetTokenRepository.save(PasswordResetToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                .usedAt(Instant.now().minus(5, ChronoUnit.MINUTES)) // Already used
                .build());

        ResetPasswordRequest resetReq = ResetPasswordRequest.builder()
                .token(rawToken)
                .newPassword("AnotherNewPassword123!")
                .build();

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test13_ResetPassword_ExpiredToken_Fails() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Expired User")
                .email("expired@example.com")
                .passwordHash(passwordEncoder.encode("OldPassword123!"))
                .build());

        String rawToken = PasswordResetTokenUtil.generateRawToken();
        String tokenHash = PasswordResetTokenUtil.hashToken(rawToken);

        passwordResetTokenRepository.save(PasswordResetToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().minus(5, ChronoUnit.MINUTES)) // Expired
                .build());

        ResetPasswordRequest resetReq = ResetPasswordRequest.builder()
                .token(rawToken)
                .newPassword("AnotherNewPassword123!")
                .build();

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test14_ForgotPassword_NewRequestInvalidatesPreviousToken() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Multi Request User")
                .email("multireq@example.com")
                .passwordHash(passwordEncoder.encode("OldPassword123!"))
                .build());

        ForgotPasswordRequest forgotReq = new ForgotPasswordRequest("multireq@example.com");

        // Request 1
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotReq)))
                .andExpect(status().isOk());

        assertEquals(1, passwordResetTokenRepository.count());
        PasswordResetToken token1 = passwordResetTokenRepository.findAll().get(0);

        // Request 2 (should delete/invalidate token 1)
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotReq)))
                .andExpect(status().isOk());

        assertEquals(1, passwordResetTokenRepository.count());
        PasswordResetToken token2 = passwordResetTokenRepository.findAll().get(0);

        assertNotEquals(token1.getId(), token2.getId());
    }
}
