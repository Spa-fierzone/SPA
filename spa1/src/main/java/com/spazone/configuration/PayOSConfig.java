package com.spazone.configuration;

import org.springframework.context.annotation.Configuration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class PayOSConfig {

    public static String CLIENT_ID = "a60fabc3-8c46-4bba-ac84-15c25097d4cc";

    public static String API_KEY = "38f0730d-7199-4dab-b158-01528dc3fd31";

    public static String CHECKSUM_KEY = "7ee9b15b2f3441745c3eee9e2196c1fb33d4749ae8bf48357d41d6767f616786";

    public static final String PAYOS_BASE_URL = "https://api-merchant.payos.vn";
    public static final String CREATE_PAYMENT_URL = PAYOS_BASE_URL + "/v2/payment-requests";

    // Tạo chữ ký HMAC SHA256
    public static String createSignature(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error creating signature", e);
        }
    }

    // Xác thực webhook signature
    public static boolean verifyWebhookSignature(String signature, String data) {
        String calculatedSignature = createSignature(data, CHECKSUM_KEY);
        return signature.equals(calculatedSignature);
    }
}