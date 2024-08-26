package com.checkout.server.rest.dto.payment;

import com.checkout.server.service.model.payment.PaymentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentStatusResponseDto {
    private String paymentId;
    private String sessionId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
}
