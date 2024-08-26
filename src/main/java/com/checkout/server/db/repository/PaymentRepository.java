package com.checkout.server.db.repository;

import com.checkout.server.db.model.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByPaymentId(String paymentId);
    Optional<PaymentEntity> findBySessionId(String sessionId);
}
