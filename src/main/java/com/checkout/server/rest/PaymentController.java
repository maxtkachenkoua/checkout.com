package com.checkout.server.rest;

import com.checkout.server.rest.dto.payment.PaymentRequestDto;
import com.checkout.server.service.PaymentService;
import com.checkout.server.service.model.payment.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequestDto paymentRequest) throws Exception {
        return ResponseEntity.ok(paymentService.createPayment(paymentRequest));
    }

    @PostMapping("/payment-callback")
    public void handleWebhook(@RequestHeader("Cko-Signature") String signature, HttpServletRequest request) throws Exception {
        paymentService
                .processPaymentCallback(StreamUtils
                        .copyToString(request.getInputStream(), StandardCharsets.UTF_8), signature);
    }
}