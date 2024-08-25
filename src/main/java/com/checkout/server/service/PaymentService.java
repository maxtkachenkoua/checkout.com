package com.checkout.server.service;

import com.checkout.server.db.model.PaymentEntity;
import com.checkout.server.db.model.UserEntity;
import com.checkout.server.db.repository.PaymentRepository;
import com.checkout.server.exception.PaymentException;
import com.checkout.server.rest.dto.payment.PaymentRequestDto;
import com.checkout.server.scheduler.PaymentStatusScheduler;
import com.checkout.server.service.model.payment.*;
import com.checkout.server.service.model.payment.PaymentRequest.CardSource;
import com.checkout.server.service.model.payment.PaymentRequest.Source;
import com.checkout.server.service.model.payment.PaymentRequest.TokenSource;
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

import static com.checkout.server.service.model.payment.PaymentStatus.fromCallbackPaymentStatus;
import static com.checkout.server.service.model.payment.PaymentStatus.fromResponsePaymentStatus;
import static com.checkout.server.util.HashingUtil.computeHmacSha256;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService extends BaseCheckoutService {
    private static final BigDecimal CENTS_HUNDRED = BigDecimal.valueOf(100);
    private static final String CHECKOUT_PAYMENTS_PATH = "/payments/";
    private final CardService cardService;
    private final UserService userService;
    private final PaymentStatusScheduler paymentStatusScheduler;
    private final PaymentRepository paymentRepository;
    @Value("${checkout.api.callback-url-signature}")
    private String callbackUrlSignature;

    public PaymentResponse createPayment(PaymentRequestDto paymentRequest) throws Exception {
        UserEntity currentUser = userService.getCurrentUser();
        log.info(String.format("[CREATE_PAYMENT][%s] - [REQUEST]:  %s", currentUser.getUsername(), paymentRequest));

        PaymentRequest checkoutPaymentRequest = PaymentRequest.builder()
                .amount(paymentRequest.getAmount().multiply(CENTS_HUNDRED))
                .currency(paymentRequest.getCurrency())
                .processingChannelId(processingChannelId)
                .source(recognizeSource(paymentRequest))
                .threeDSecure(paymentRequest.getUse3Ds() ? PaymentRequest.ThreeDSecure.builder().build() : null)
                .build();

        ResponseEntity<PaymentResponse> response;
        try {
            response = restTemplate.exchange(checkoutApiUrl + CHECKOUT_PAYMENTS_PATH,
                    HttpMethod.POST,
                    new HttpEntity<>(checkoutPaymentRequest, authHeaderSecretKey()),
                    PaymentResponse.class);

            String submittedPaymentId = Objects.requireNonNull(response.getBody()).getId();
            PaymentStatus paymentStatus = PaymentStatus.fromResponsePaymentStatus(response.getBody().getStatus());

            // persist submitted payment
            paymentRepository.save(PaymentEntity.builder()
                    .paymentType(paymentRequest.getPaymentType())
                    .paymentId(submittedPaymentId)
                    .userId(currentUser.getId())
                    .amount(paymentRequest.getAmount())
                    .currency(paymentRequest.getCurrency())
                    .status(paymentStatus)
                    .build());

            // schedule possible pending status check
            if (PaymentStatus.PENDING == paymentStatus) {
                paymentStatusScheduler.schedulePaymentStatusCheck(submittedPaymentId);
            }

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

    public PaymentStatusResponse getPaymentStatus(String paymentId) {
        return restTemplate.exchange(checkoutApiUrl + CHECKOUT_PAYMENTS_PATH + paymentId,
                HttpMethod.GET,
                new HttpEntity<>(authHeaderSecretKey()),
                PaymentStatusResponse.class
        ).getBody();
    }

    public void processPaymentCallback(String requestBody, String signature) throws Exception {
        String hash = computeHmacSha256(requestBody, callbackUrlSignature);
        if (hash.equals(signature)) {
            PaymentCallbackResponse callbackRequest = objectMapper.readValue(requestBody, PaymentCallbackResponse.class);
            PaymentStatus paymentStatus = fromCallbackPaymentStatus(callbackRequest.getType());
            log.info(String.format("[PAYMENT_CALLBACK] [%s:%s]", callbackRequest.getData().getId(), paymentStatus));

            String paymentId = callbackRequest.getData().getId();

            PaymentStatusResponse paymentStatusResponse = getPaymentStatus(paymentId);
            PaymentStatus confirmPaymentStatus = fromResponsePaymentStatus(paymentStatusResponse.getStatus());

            if (paymentStatus == confirmPaymentStatus) {
                PaymentEntity payment = getPayment(paymentId);
                payment.setStatus(fromCallbackPaymentStatus(callbackRequest.getType()));
                paymentRepository.save(payment);
            }
        } else {
            log.warn(String.format("[CALLBACK_URL_SIGNATURE_FAILURE] [%s] : [%s]", signature, requestBody));
        }
    }

    public PaymentStatus getPaymentDbStatus(String paymentId) {
        return getPayment(paymentId).getStatus();
    }

    public boolean updatePendingPaymentStatus(String paymentId) {
        PaymentEntity payment = getPayment(paymentId);
        PaymentStatusResponse response = getPaymentStatus(paymentId);
        PaymentStatus checkoutPaymentStatus = PaymentStatus.fromCallbackPaymentStatus(response.getStatus());
        if (PaymentStatus.PENDING != checkoutPaymentStatus && PaymentStatus.PENDING == payment.getStatus()) {
            payment.setStatus(checkoutPaymentStatus);
            paymentRepository.save(payment);
            return true;
        }
        return false;
    }

    private PaymentEntity getPayment(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId).orElseThrow();
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
