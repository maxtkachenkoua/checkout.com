package com.checkout.server.service.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentRequest {
    private BigDecimal amount;
    @JsonProperty("processing_channel_id")
    private String processingChannelId;
    private String currency;
    private Source source;
    @JsonProperty("3ds")
    private ThreeDSecure threeDSecure;

    public interface Source {
    }

    @Data
    @Builder
    public static class CardSource implements Source {
        private final String type = "card";
        private String number;
        @JsonProperty("expiry_month")
        private String expiryMonth;
        @JsonProperty("expiry_year")
        private String expiryYear;
        private String cvv;
    }

    @Data
    @Builder
    public static class TokenSource implements Source {
        @JsonProperty("type")
        private final String type = "token";
        @JsonProperty("token")
        private String token;
    }

    @Data
    @Builder
    public static class ThreeDSecure {
        private final boolean enabled = true;
        private final String attemptN3D = "Y";
    }
}
