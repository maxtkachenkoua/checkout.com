package com.checkout.server.service.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentResponse {
    private String id;
    private String status;
    @JsonProperty("_links")
    private Links links;

    @Data
    public static class Links {
        private Redirect redirect;

        @Data
        private static class Redirect {
            @JsonProperty("href")
            private String redirectUrl;
        }
    }
}
