package com.globetrotter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignupRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_#^()~+=\\-\\[\\]{}|;:',.<>?/])[A-Za-z\\d@$!%*?&_#^()~+=\\-\\[\\]{}|;:',.<>?/]{8,}$",
            message = "Password must be at least 8 characters long and contain at least 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character"
    )
    private String password;

    public SignupRequest() {
    }

    public SignupRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static SignupRequestBuilder builder() {
        return new SignupRequestBuilder();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public static class SignupRequestBuilder {
        private String name;
        private String email;
        private String password;

        public SignupRequestBuilder name(String name) { this.name = name; return this; }
        public SignupRequestBuilder email(String email) { this.email = email; return this; }
        public SignupRequestBuilder password(String password) { this.password = password; return this; }

        public SignupRequest build() {
            return new SignupRequest(name, email, password);
        }
    }
}
