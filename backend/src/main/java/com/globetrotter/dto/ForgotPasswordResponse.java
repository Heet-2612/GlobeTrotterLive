package com.globetrotter.dto;

public class ForgotPasswordResponse {

    private String message;

    public ForgotPasswordResponse() {
    }

    public ForgotPasswordResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ForgotPasswordResponseBuilder builder() {
        return new ForgotPasswordResponseBuilder();
    }

    public static class ForgotPasswordResponseBuilder {
        private String message;

        public ForgotPasswordResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ForgotPasswordResponse build() {
            return new ForgotPasswordResponse(message);
        }
    }
}
