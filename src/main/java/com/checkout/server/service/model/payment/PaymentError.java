package com.checkout.server.service.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PaymentError {
    @JsonProperty("error_codes")
    private List<String> errorCodes;
}
