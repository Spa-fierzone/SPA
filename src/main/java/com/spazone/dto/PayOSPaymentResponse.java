package com.spazone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// Response models
public class PayOSPaymentResponse {
    @JsonProperty("error")
    private int error;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private PayOSPaymentData data;

    // Getters and Setters
    public int getError() { return error; }
    public void setError(int error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public PayOSPaymentData getData() { return data; }
    public void setData(PayOSPaymentData data) { this.data = data; }
}
