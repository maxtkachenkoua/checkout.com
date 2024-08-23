package com.checkout.server.rest.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class PaymentRequestDto {
    @NotBlank(message = "cardNumber field is not set")
    @Size(min = 16, max = 16, message = "cardNumber must be 16 digits")
    private String cardNumber;
    @NotBlank(message = "expiryMonth field is not set")
    @Size(min = 2, max = 2, message = "expiryMonth must be 16 digits")
    private String expiryMonth;
    @NotBlank(message = "expiryYear field is not set")
    @Size(min = 2, max = 2, message = "expiryYear must be 16 digits")
    private String expiryYear;
    @NotBlank(message = "cvv field is not set")
    @Size(min = 3, max = 3, message = "cvv must be 3 digits")
    private String cvv;
    @NotNull(message = "amount field is not set")
    @Positive(message = "amount field should be positive")
    private BigDecimal amount;
    @NotNull(message = "currency field is not set")
    private String currency;
    @NotNull(message = "paymentType field is not set. Available values: [CARD_INFO, CARD_TOKEN]")
    private PaymentType paymentType;
}
