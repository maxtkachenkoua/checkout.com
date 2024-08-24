package com.checkout.server.scheduler;

import com.checkout.server.service.PaymentService;
import com.checkout.server.service.model.payment.PaymentStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@EnableAsync
public class PaymentStatusScheduler {
    private final PaymentService paymentService;
    private final ScheduledExecutorService scheduler;
    @Value("${scheduler.max-attempts}")
    private long maxAttempts;
    @Value("${scheduler.initial-delay-after-payment-request}")
    private long initialDelayAfterPaymentRequest;
    @Value("${scheduler.period-between-attempts}")
    private long periodBetweenAttempts;

    public PaymentStatusScheduler(@Lazy PaymentService paymentService,
                                  @Value("${scheduler.thread-pool-size}") int threadPoolSize) {
        this.paymentService = paymentService;
        this.scheduler = Executors.newScheduledThreadPool(threadPoolSize);
    }

    @Async
    public void schedulePaymentStatusCheck(String paymentId) {
        final AtomicInteger attempts = new AtomicInteger(BigDecimal.ZERO.intValue());
        ScheduledFutureHolder scheduledFutureHolder = new ScheduledFutureHolder();
        Runnable task = () -> {
            if (attempts.get() < maxAttempts) {
                log.info(String.format("[PAYMENT_STATUS_SCHEDULER] [%s] %d attempt. Max attempts: %d", paymentId, attempts.get() + 1, maxAttempts));
                PaymentStatus paymentStatus = paymentService.getPaymentDbStatus(paymentId);
                if (PaymentStatus.PENDING != paymentStatus) {
                    scheduledFutureHolder.cancel();
                    log.info(String.format("[PAYMENT_STATUS_SCHEDULER] [%s:%s] No scheduler required", paymentId, paymentStatus));
                    return;
                }

                if (paymentService.updatePendingPaymentStatus(paymentId)) {
                    log.info(String.format("[PAYMENT_STATUS_SCHEDULER] [%s] Payment status updated", paymentId));
                    scheduledFutureHolder.cancel();
                    return;
                }
                attempts.incrementAndGet();
            } else {
                scheduledFutureHolder.cancel();
                log.info(String.format("[PAYMENT_STATUS_SCHEDULER] [%s] Exceeded max retry attempts", paymentId));
            }
        };
        scheduledFutureHolder.setScheduledFuture(scheduler
                .scheduleAtFixedRate(task, initialDelayAfterPaymentRequest, periodBetweenAttempts, TimeUnit.SECONDS));
        log.info(String.format("[PAYMENT_STATUS_SCHEDULER][%s][SUBMITTED]. First retry in %d seconds. Period between attempts is %d seconds",
                paymentId, initialDelayAfterPaymentRequest, periodBetweenAttempts));
    }
}

@Data
class ScheduledFutureHolder {
    private ScheduledFuture<?> scheduledFuture;

    public void cancel() {
        scheduledFuture.cancel(false);
    }
}


