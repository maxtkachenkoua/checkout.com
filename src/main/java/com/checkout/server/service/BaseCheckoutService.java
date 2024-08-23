package com.checkout.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BaseCheckoutService {
    @Autowired
    protected ModelMapper modelMapper;
    @Autowired
    protected RestTemplate restTemplate;
    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${checkout.api.url}")
    protected String checkoutApiUrl;

    @Value("${checkout.api.secret-key}")
    protected String checkoutSecretKey;

    @Value("${checkout.api.public-api-key}")
    protected String checkoutPublicApiKey;

    @Value("${checkout.api.processing-channel-id}")
    protected String processingChannelId;

    protected HttpHeaders authHeaderSecretKey() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + checkoutSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    protected HttpHeaders authHeaderPublicApiKey() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + checkoutPublicApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
