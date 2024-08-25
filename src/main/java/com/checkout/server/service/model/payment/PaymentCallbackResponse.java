package com.checkout.server.service.model.payment;

import lombok.Data;

@Data
public class PaymentCallbackResponse {
    private String type;
    private PaymentData data;

    @Data
    public static class PaymentData {
        private String id;
    }
}
