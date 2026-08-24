package com.globetrotter.config;

import com.globetrotter.entity.Role;
import com.globetrotter.entity.User;
import com.globetrotter.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.initial.email:}")
    private String adminEmail;

    @Value("${admin.initial.password:}")
    private String adminPassword;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (adminEmail == null || adminEmail.trim().isEmpty() || adminPassword == null || adminPassword.trim().isEmpty()) {
            logger.info("Admin initialization skipped. Please provide admin.initial.email and admin.initial.password environment variables to create the first admin.");
            return;
        }

        String normalizedEmail = adminEmail.trim().toLowerCase();
        Optional<User> existingUser = userRepository.findByEmail(normalizedEmail);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getRole() != Role.ADMIN) {
                user.setRole(Role.ADMIN);
                userRepository.save(user);
                logger.info("Existing user {} has been promoted to ADMIN.", normalizedEmail);
            }
        } else {
            User admin = User.builder()
                    .name("Administrator")
                    .email(normalizedEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .languagePreference("en")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            logger.info("Initial admin user {} has been created successfully.", normalizedEmail);
        }
        
        // After setup, clear it from memory to avoid holding secrets
        adminPassword = null;
    }
}
