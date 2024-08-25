package com.checkout.server.db.repository;

import com.checkout.server.db.model.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    void deleteAllByUserId(Long userId);
}
