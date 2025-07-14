package com.spazone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// Request models
public class PayOSPaymentRequest {
    @JsonProperty("orderCode")
    private long orderCode;

    @JsonProperty("amount")
    private int amount;

    @JsonProperty("description")
    private String description;

    @JsonProperty("items")
    private List<PayOSItem> items;

    @JsonProperty("returnUrl")
    private String returnUrl;

    @JsonProperty("cancelUrl")
    private String cancelUrl;

    @JsonProperty("signature")
    private String signature;

    // Constructors
    public PayOSPaymentRequest() {}

    public PayOSPaymentRequest(long orderCode, int amount, String description,
                               List<PayOSItem> items, String returnUrl, String cancelUrl) {
        this.orderCode = orderCode;
        this.amount = amount;
        this.description = description;
        this.items = items;
        this.returnUrl = returnUrl;
        this.cancelUrl = cancelUrl;
    }

    // Getters and Setters
    public long getOrderCode() { return orderCode; }
    public void setOrderCode(long orderCode) { this.orderCode = orderCode; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<PayOSItem> getItems() { return items; }
    public void setItems(List<PayOSItem> items) { this.items = items; }

    public String getReturnUrl() { return returnUrl; }
    public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }

    public String getCancelUrl() { return cancelUrl; }
    public void setCancelUrl(String cancelUrl) { this.cancelUrl = cancelUrl; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}

