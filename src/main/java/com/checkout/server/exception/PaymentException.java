package com.checkout.server.exception;

import com.checkout.server.service.model.PaymentError;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class PaymentException extends Exception{
    private final List<String> paymentErrors;
}
