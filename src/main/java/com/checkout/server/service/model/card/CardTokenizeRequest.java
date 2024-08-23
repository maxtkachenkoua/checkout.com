package com.checkout.server.service.model.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardTokenizeRequest {
    private final String type = "card";
    @JsonProperty("number")
    private String cardNumber;

    @JsonProperty("expiry_month")
    private String expiryMonth;

    @JsonProperty("expiry_year")
    private String expiryYear;

    @JsonProperty("cvv")
    private String cvv;
}
