package com.globetrotter.service;

import com.globetrotter.dto.*;
import com.globetrotter.entity.PasswordResetToken;
import com.globetrotter.entity.User;
import com.globetrotter.exception.InvalidCredentialsException;
import com.globetrotter.exception.UserAlreadyExistsException;
import com.globetrotter.repository.PasswordResetTokenRepository;
import com.globetrotter.repository.UserRepository;
import com.globetrotter.security.JwtTokenProvider;
import com.globetrotter.security.PasswordResetTokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final BrevoEmailService brevoEmailService;

    public AuthService(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            BrevoEmailService brevoEmailService
    ) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.brevoEmailService = brevoEmailService;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException("Email is already registered: " + normalizedEmail);
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = User.builder()
                .name(request.getName().trim())
                .email(normalizedEmail)
                .passwordHash(hashedPassword)
                .languagePreference("en")
                .role(com.globetrotter.entity.Role.USER)
                .build();

        User savedUser = userRepository.save(newUser);
        String token = tokenProvider.generateToken(savedUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.fromEntity(savedUser))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = tokenProvider.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.fromEntity(user))
                .build();
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        String genericMessage = "If an account exists for this email, password reset instructions have been sent.";

        Optional<User> userOptional = userRepository.findByEmail(normalizedEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Invalidate/delete existing active reset tokens for user
            passwordResetTokenRepository.deleteByUser(user);

            // Generate secure token & SHA-256 hash
            String rawToken = PasswordResetTokenUtil.generateRawToken();
            String tokenHash = PasswordResetTokenUtil.hashToken(rawToken);

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .user(user)
                    .tokenHash(tokenHash)
                    .expiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                    .build();

            passwordResetTokenRepository.save(resetToken);

            // Trigger Brevo transactional email
            brevoEmailService.sendPasswordResetEmail(user.getEmail(), user.getName(), rawToken);
        }

        return ForgotPasswordResponse.builder()
                .message(genericMessage)
                .build();
    }

    @Transactional
    public ForgotPasswordResponse resetPassword(ResetPasswordRequest request) {
        String tokenHash = PasswordResetTokenUtil.hashToken(request.getToken().trim());

        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid or expired password reset token"));

        if (resetToken.getUsedAt() != null || resetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidCredentialsException("Invalid or expired password reset token");
        }

        User user = resetToken.getUser();

        // Update password with BCrypt
        String newHashedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(newHashedPassword);
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsedAt(Instant.now());
        passwordResetTokenRepository.save(resetToken);

        return ForgotPasswordResponse.builder()
                .message("Your password has been reset successfully.")
                .build();
    }
}
