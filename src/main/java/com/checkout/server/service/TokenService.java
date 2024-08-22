package com.checkout.server.service;

import com.checkout.server.db.model.TokenEntity;
import com.checkout.server.db.model.UserEntity;
import com.checkout.server.db.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public void updateUserToken(UserEntity user, String token) {
        tokenRepository.deleteAllByUserId(user.getId());
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUser(user);
        tokenRepository.save(tokenEntity);
    }
}
