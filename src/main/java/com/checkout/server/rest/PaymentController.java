package com.checkout.server.rest;

import com.checkout.server.rest.dto.payment.PaymentRequestDto;
import com.checkout.server.rest.dto.payment.PaymentResponseDto;
import com.checkout.server.rest.dto.payment.PaymentStatusResponseDto;
import com.checkout.server.service.PaymentService;
import com.checkout.server.service.model.payment.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Payments", description = "Endpoints for handling payments and payment callbacks")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Process a payment", description = "Process a payment with the provided payment details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment processed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @PostMapping("${checkout.urls.payment-url}")
    public ResponseEntity<PaymentResponseDto> processPayment(@Valid @RequestBody PaymentRequestDto paymentRequest) throws Exception {
        return ResponseEntity.ok(paymentService.createPayment(paymentRequest));
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<PaymentStatusResponseDto> getPaymentStatus(@PathVariable("id") String paymentId) {
        return ResponseEntity.ok(paymentService.pollPaymentStatus(paymentId));
    }

    @Operation(summary = "Handle payment callback", description = "Handles webhook notifications from Checkout.com")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook processed successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid webhook data",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized request",
                    content = @Content)
    })
    @PostMapping("${checkout.urls.payment-callback-url}")
    public void handleWebhook(@RequestHeader("Cko-Signature") String signature, HttpServletRequest request) throws Exception {
        paymentService.processPaymentCallback(StreamUtils
                .copyToString(request.getInputStream(), StandardCharsets.UTF_8), signature);
    }
}