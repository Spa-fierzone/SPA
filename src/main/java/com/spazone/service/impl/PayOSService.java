package com.spazone.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spazone.configuration.PayOSConfig;
import com.spazone.dto.PayOSPaymentRequest;
import com.spazone.dto.PayOSPaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PayOSService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public PayOSPaymentResponse createPaymentLink(PayOSPaymentRequest request) {
        try {
            // Tạo signature
            String signatureData = String.format("amount=%d&cancelUrl=%s&description=%s&orderCode=%d&returnUrl=%s",
                    request.getAmount(),
                    request.getCancelUrl(),
                    request.getDescription(),
                    request.getOrderCode(),
                    request.getReturnUrl());

            String signature = PayOSConfig.createSignature(signatureData, PayOSConfig.CHECKSUM_KEY);
            request.setSignature(signature);

            // Tạo headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-client-id", PayOSConfig.CLIENT_ID);
            headers.set("x-api-key", PayOSConfig.API_KEY);

            // Tạo request entity
            HttpEntity<PayOSPaymentRequest> entity = new HttpEntity<>(request, headers);

            // Gọi API
            ResponseEntity<PayOSPaymentResponse> response = restTemplate.exchange(
                    PayOSConfig.CREATE_PAYMENT_URL,
                    HttpMethod.POST,
                    entity,
                    PayOSPaymentResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error creating PayOS payment link", e);
        }
    }

    public PayOSPaymentResponse getPaymentInfo(long orderCode) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-client-id", PayOSConfig.CLIENT_ID);
            headers.set("x-api-key", PayOSConfig.API_KEY);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            String url = PayOSConfig.PAYOS_BASE_URL + "/v2/payment-requests/" + orderCode;

            ResponseEntity<PayOSPaymentResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    PayOSPaymentResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error getting PayOS payment info", e);
        }
    }

    public boolean cancelPayment(long orderCode, String reason) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-client-id", PayOSConfig.CLIENT_ID);
            headers.set("x-api-key", PayOSConfig.API_KEY);

            String requestBody = String.format("{\"cancellationReason\":\"%s\"}", reason);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String url = PayOSConfig.PAYOS_BASE_URL + "/v2/payment-requests/" + orderCode + "/cancel";

            ResponseEntity<PayOSPaymentResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    PayOSPaymentResponse.class
            );

            return response.getStatusCode() == HttpStatus.OK;

        } catch (Exception e) {
            throw new RuntimeException("Error canceling PayOS payment", e);
        }
    }
}