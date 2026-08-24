package com.globetrotter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ResetPasswordRequest {

    @NotBlank(message = "Reset token is required")
    private String token;

    @NotBlank(message = "New password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    private String newPassword;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public static ResetPasswordRequestBuilder builder() {
        return new ResetPasswordRequestBuilder();
    }

    public static class ResetPasswordRequestBuilder {
        private String token;
        private String newPassword;

        public ResetPasswordRequestBuilder token(String token) {
            this.token = token;
            return this;
        }

        public ResetPasswordRequestBuilder newPassword(String newPassword) {
            this.newPassword = newPassword;
            return this;
        }

        public ResetPasswordRequest build() {
            return new ResetPasswordRequest(token, newPassword);
        }
    }
}
