package com.checkout.server.rest.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponseDto {
    private String id;
    private String status;
    private String redirectUrl;
}
