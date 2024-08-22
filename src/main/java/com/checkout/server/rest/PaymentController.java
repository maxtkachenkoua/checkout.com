package com.checkout.server.rest;

import com.checkout.server.rest.dto.payment.PaymentRequestDto;
import com.checkout.server.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")

public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<Map> processPayment(@Valid @RequestBody PaymentRequestDto paymentRequest) throws Exception {
        Map processedPayment = paymentService.createPayment(paymentRequest);
        return ResponseEntity.ok(processedPayment);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody Map<String, Object> webhookPayload) {
        paymentService.handleWebhook(webhookPayload);
        return ResponseEntity.ok().build();
    }
}