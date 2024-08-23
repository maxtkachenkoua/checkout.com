package com.checkout.server.service;

import com.checkout.server.rest.dto.payment.PaymentRequestDto;
import com.checkout.server.service.model.card.CardTokenizeRequest;
import com.checkout.server.service.model.card.CardTokenizeResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CardService extends BaseCheckoutService {
    private static final String CHECKOUT_TOKENIZE_CARD_PATH = "/tokens";

    public CardTokenizeResponse tokenizeCard(PaymentRequestDto cardTokenizeRequest) throws Exception {
        HttpEntity<CardTokenizeRequest> request = new HttpEntity<>(modelMapper.map(cardTokenizeRequest, CardTokenizeRequest.class), authHeaderPublicApiKey());
        ResponseEntity<CardTokenizeResponse> response = restTemplate
                .exchange(checkoutApiUrl + CHECKOUT_TOKENIZE_CARD_PATH, HttpMethod.POST, request, CardTokenizeResponse.class);
        return response.getBody();
    }
}
