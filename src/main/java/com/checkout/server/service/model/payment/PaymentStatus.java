package com.checkout.server.service.model.payment;

public enum PaymentStatus {
    PENDING,
    AUTHORIZED,
    CAPTURED,
    DECLINED,
    FAILED,
    CANCELED,
    REFUNDED,
    PARTIALLY_REFUNDED,
    EXPIRED,
    CHARGEBACK,
    VOIDED,
    PROCESSING,
    COMPLETED;

    public static PaymentStatus fromResponsePaymentStatus(String status) {
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        return PaymentStatus.valueOf(status.trim().toUpperCase());
    }

    public static PaymentStatus fromCallbackPaymentStatus(String status) {
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        return PaymentStatus.valueOf(status.trim().toUpperCase().replace("PAYMENT_", ""));
    }

}

