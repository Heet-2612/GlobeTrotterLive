package com.globetrotter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BrevoEmailService {

    private static final Logger log = LoggerFactory.getLogger(BrevoEmailService.class);

    @Value("${brevo.api-key:${BREVO_API_KEY:}}")
    private String apiKey;

    @Value("${brevo.sender-email:${BREVO_SENDER_EMAIL:}}")
    private String senderEmail;

    @Value("${brevo.sender-name:${BREVO_SENDER_NAME:GlobeTrotter}}")
    private String senderName;

    @Value("${app.frontend-url:${FRONTEND_BASE_URL:http://localhost:3003}}")
    private String frontendUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendPasswordResetEmail(String recipientEmail, String recipientName, String rawToken) {
        String resetUrl = frontendUrl.replaceAll("/+$", "") + "/#reset-password?token=" + rawToken;

        if (apiKey == null || apiKey.trim().isEmpty() || senderEmail == null || senderEmail.trim().isEmpty()) {
            log.warn("Brevo API credentials not configured (BREVO_API_KEY or BREVO_SENDER_EMAIL missing). Password reset email skipped for recipient: {}", recipientEmail);
            return false;
        }

        try {
            String brevoApiEndpoint = "https://api.brevo.com/v3/smtp/email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey.trim());

            Map<String, Object> senderMap = new HashMap<>();
            senderMap.put("name", senderName);
            senderMap.put("email", senderEmail.trim());

            Map<String, Object> recipientMap = new HashMap<>();
            recipientMap.put("email", recipientEmail.trim());
            if (recipientName != null && !recipientName.trim().isEmpty()) {
                recipientMap.put("name", recipientName.trim());
            }

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <title>Reset Your Password</title>
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f5f7f6; margin: 0; padding: 20px;">
                    <div style="max-width: 550px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 16px; border: 1px solid #e2e8f0;">
                        <h2 style="color: #0f172a; margin-top: 0;">Globe<span style="color: #10b981;">Trotter</span></h2>
                        <h3 style="color: #1e293b;">Reset your password</h3>
                        <p style="color: #475569; font-size: 14px; line-height: 1.6;">
                            We received a request to reset your password for your GlobeTrotter account. Click the button below to reset it:
                        </p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #10b981; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 14px; display: inline-block;">
                                Reset Password
                            </a>
                        </div>
                        <p style="color: #64748b; font-size: 12px;">
                            ⏰ Note: This password reset link will expire in <strong>30 minutes</strong> and can only be used once.
                        </p>
                        <hr style="border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;" />
                        <p style="color: #94a3b8; font-size: 11px;">
                            If you did not request a password reset, please ignore this email. Your password will remain unchanged.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(resetUrl);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", senderMap);
            body.put("to", List.of(recipientMap));
            body.put("subject", "Reset your GlobeTrotter password");
            body.put("htmlContent", htmlContent);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(brevoApiEndpoint, requestEntity, String.class);

            log.info("Successfully sent Brevo password reset email to {}", recipientEmail);
            return true;
        } catch (Exception e) {
            log.error("Failed to send password reset email via Brevo for email {}: {}", recipientEmail, e.getMessage());
            return false;
        }
    }
}
