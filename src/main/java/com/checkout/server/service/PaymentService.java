package com.checkout.server.service;

import com.checkout.server.db.model.PaymentEntity;
import com.checkout.server.db.model.UserEntity;
import com.checkout.server.db.repository.PaymentRepository;
import com.checkout.server.exception.PaymentException;
import com.checkout.server.rest.dto.payment.PaymentRequestDto;
import com.checkout.server.service.model.payment.PaymentError;
import com.checkout.server.service.model.payment.PaymentRequest;
import com.checkout.server.service.model.payment.PaymentRequest.CardSource;
import com.checkout.server.service.model.payment.PaymentRequest.Source;
import com.checkout.server.service.model.payment.PaymentRequest.TokenSource;
import com.checkout.server.service.model.payment.PaymentResponse;
import com.checkout.server.service.model.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.Objects;

import static com.checkout.server.util.HashingUtil.computeHmacSha256;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService extends BaseCheckoutService {
    private static final BigDecimal CENTS_HUNDRED = BigDecimal.valueOf(100);
    private static final String CHECKOUT_PAYMENTS_PATH = "/payments";

    @Value("${checkout.api.callback-url-signature}")
    private String callbackUrlSignature;
    private final CardService cardService;
    private final UserService userService;
    private final PaymentRepository paymentRepository;

    public PaymentResponse createPayment(PaymentRequestDto paymentRequest) throws Exception {
        UserEntity currentUser = userService.getCurrentUser();
        log.info(String.format("[CREATE_PAYMENT][%s] - [REQUEST]:  %s", currentUser.getUsername(), paymentRequest));

        PaymentRequest checkoutPaymentRequest = PaymentRequest.builder()
                .amount(paymentRequest.getAmount().multiply(CENTS_HUNDRED))
                .currency(paymentRequest.getCurrency())
                .processingChannelId(processingChannelId)
                .source(recognizeSource(paymentRequest))
                .build();
        ResponseEntity<PaymentResponse> response;
        try {
            response = restTemplate.exchange(checkoutApiUrl + CHECKOUT_PAYMENTS_PATH,
                    HttpMethod.POST,
                    new HttpEntity<>(checkoutPaymentRequest, authHeaderSecretKey()),
                    PaymentResponse.class);
            paymentRepository.save(PaymentEntity.builder()
                    .paymentType(paymentRequest.getPaymentType())
                    .paymentId(Objects.requireNonNull(response.getBody()).getId())
                    .userId(currentUser.getId())
                    .amount(paymentRequest.getAmount())
                    .currency(paymentRequest.getCurrency())
                    .status(PaymentStatus.toPaymentStatus(response.getBody().getStatus()))
                    .build());

            log.info(String.format("[CREATE_PAYMENT][%s] - [SUBMITTED:HTTP %d]:  %s",
                    currentUser.getUsername(), response.getStatusCode().value(), response.getBody()));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == UNPROCESSABLE_ENTITY) {
                throw new PaymentException(objectMapper.readValue(e.getResponseBodyAsString(), PaymentError.class)
                        .getErrorCodes());
            }
            throw e;
        }
        return response.getBody();
    }

    public void processPaymentCallback(String requestBody, String signature) throws Exception {
        String hash = computeHmacSha256(requestBody, callbackUrlSignature);

        if (hash.equals(signature)) {
            System.out.println("opa nihua");
        } else {
            log.warn(String.format("[CALLBACK_URL_SIGNATURE_FAILURE] [%s] : [%s]", signature, requestBody));
        }
    }

    private Source recognizeSource(PaymentRequestDto paymentRequest) throws Exception {
        return switch (paymentRequest.getPaymentType()) {
            case CARD_INFO -> cardInfoSource(paymentRequest);
            case CARD_TOKEN -> cardTokenSource(paymentRequest);
        };
    }

    private Source cardInfoSource(PaymentRequestDto paymentRequest) {
        return CardSource.builder()
                .number(paymentRequest.getCardNumber())
                .expiryMonth(paymentRequest.getExpiryMonth())
                .expiryYear(paymentRequest.getExpiryYear())
                .cvv(paymentRequest.getCvv())
                .build();
    }

    private Source cardTokenSource(PaymentRequestDto paymentRequest) throws Exception {
        return TokenSource.builder()
                .token(cardService.tokenizeCard(paymentRequest).getToken())
                .build();
    }
}
