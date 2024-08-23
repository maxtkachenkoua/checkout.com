package com.checkout.server.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class PaymentException extends Exception{
    private final List<String> paymentErrors;
}
