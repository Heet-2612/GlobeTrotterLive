package com.globetrotter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class DotenvLoadingTest {

    @Autowired
    private Environment environment;

    @Value("${brevo.sender-name:}")
    private String senderName;

    @Value("${brevo.api-key:}")
    private String apiKey;

    @Value("${brevo.sender-email:}")
    private String senderEmail;

    @Test
    void testRootDotenvFileLoaded() {
        // Verify properties are present without printing secret values
        assertThat(environment.containsProperty("BREVO_API_KEY")).isTrue();
        assertThat(apiKey).isNotEmpty();
        assertThat(senderEmail).isNotEmpty();
        assertThat(senderName).isEqualTo("GlobeTrotter");
    }
}
