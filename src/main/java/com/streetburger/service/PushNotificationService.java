package com.streetburger.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PushNotificationService {

    private final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Send a push notification to a list of tokens
     */
    public void sendPushNotification(List<String> tokens, String title, String body, Map<String, Object> data) {
        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        // Expo allows sending multiple messages in one request
        // For simplicity, we send them one by one or in small batches
        // Here we send one request per token to keep it robust for this stage
        for (String token : tokens) {
            sendToSingleToken(token, title, body, data);
        }
    }

    private void sendToSingleToken(String token, String title, String body, Map<String, Object> data) {
        if (token == null || !token.startsWith("ExponentPushToken")) {
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = new HashMap<>();
            payload.put("to", token);
            payload.put("title", title);
            payload.put("body", body);
            payload.put("sound", "default");
            if (data != null) {
                payload.put("data", data);
            }

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);

            System.out.println("Push notification sent to: " + token);
        } catch (Exception e) {
            System.err.println("Failed to send push notification to " + token + ": " + e.getMessage());
        }
    }
}
