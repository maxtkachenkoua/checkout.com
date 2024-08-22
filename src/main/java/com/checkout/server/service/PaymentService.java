package com.checkout.server.service;

import com.checkout.server.exception.PaymentException;
import com.checkout.server.rest.dto.payment.PaymentRequestDto;
import com.checkout.server.service.model.PaymentError;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${checkout.api.url}")
    private String checkoutApiUrl;

    @Value("${checkout.api.key}")
    private String checkoutApiKey;

    @Value("${checkout.api.processing-channel-id}")
    private String processingChannelId;


    public Map createPayment(PaymentRequestDto paymentRequest) throws Exception {
        HttpHeaders authHeaders = authHeaders();

        Map<String, Object> body = new HashMap<>();
        body.put("source", Map.of(
                "type", "card",
                "number", paymentRequest.getCardNumber(),
                "expiry_month", paymentRequest.getExpiryDate().split("/")[0],
                "expiry_year", paymentRequest.getExpiryDate().split("/")[1],
                "cvv", paymentRequest.getCvv()
        ));
        body.put("amount", paymentRequest.getAmount().multiply(BigDecimal.valueOf(100))); // Amount in cents
        body.put("processing_channel_id", processingChannelId);
        body.put("currency", "USD");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, authHeaders);
        ResponseEntity<Map> response = null;
        try {
            response = restTemplate.exchange(checkoutApiUrl + "/payments", HttpMethod.POST, request, Map.class);
        } catch (HttpClientErrorException e) {

            if (e.getStatusCode() == UNPROCESSABLE_ENTITY) {
                throw new PaymentException(objectMapper.readValue(e.getResponseBodyAsString(), PaymentError.class).getErrorCodes());
            }
            if (e.getStatusCode() == HttpStatus.OK) {

            } else {

            }
            throw e;
        }


        return response.getBody();
    }

    public void handleWebhook(Map<String, Object> webhookPayload) {
        // Process webhook notification and update payment status accordingly
        String paymentId = (String) webhookPayload.get("id");
        String status = (String) webhookPayload.get("status");
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + checkoutApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}
