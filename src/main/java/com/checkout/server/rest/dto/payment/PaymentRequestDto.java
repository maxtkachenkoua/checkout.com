package com.checkout.server.rest.dto.payment;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {
    @NotBlank(message = "cardNumber field is not set")
    @Size(min = 16, max = 16, message = "cardNumber must be 16 digits")
    private String cardNumber;
    @NotBlank(message = "expiryDate field is not set")
    @Pattern(regexp = "(0[1-9]|1[0-2])/([0-9]{2})", message = "expiryDate must be in MM/YY format")
    private String expiryDate;
    @NotBlank(message = "cvv field is not set")
    @Size(min = 3, max = 3, message = "cvv must be 3 digits")
    private String cvv;
    @NotNull(message = "amount field is not set")
    @Positive(message = "amount field should be positive")
    private BigDecimal amount;
}
