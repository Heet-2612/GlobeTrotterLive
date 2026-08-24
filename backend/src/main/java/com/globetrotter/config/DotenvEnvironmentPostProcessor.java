package com.globetrotter.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path userDir = Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();

        Path[] candidatePaths = new Path[]{
                userDir.resolve(".env"),
                userDir.getParent() != null ? userDir.getParent().resolve(".env") : null,
                Paths.get(".env").toAbsolutePath().normalize(),
                Paths.get("../.env").toAbsolutePath().normalize()
        };

        File envFile = null;
        for (Path candidate : candidatePaths) {
            if (candidate != null && Files.exists(candidate) && Files.isRegularFile(candidate)) {
                envFile = candidate.toFile();
                break;
            }
        }

        if (envFile != null && envFile.canRead()) {
            try {
                List<String> lines = Files.readAllLines(envFile.toPath());
                Map<String, Object> dotenvMap = new HashMap<>();

                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                        continue;
                    }
                    int eqIndex = trimmed.indexOf('=');
                    if (eqIndex > 0) {
                        String key = trimmed.substring(0, eqIndex).trim();
                        String value = trimmed.substring(eqIndex + 1).trim();
                        if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
                            if (value.length() >= 2) {
                                value = value.substring(1, value.length() - 1);
                            }
                        }
                        if (!value.isEmpty()) {
                            dotenvMap.put(key, value);
                        }
                    }
                }

                if (!dotenvMap.isEmpty()) {
                    environment.getPropertySources().addFirst(new MapPropertySource("rootDotenvProperties", dotenvMap));
                }
            } catch (Exception ignored) {
                // Silently ignore errors reading .env
            }
        }
    }
}
