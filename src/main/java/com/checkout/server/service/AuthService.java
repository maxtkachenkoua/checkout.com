package com.checkout.server.service;

import com.checkout.server.db.model.UserEntity;
import com.checkout.server.exception.InvalidUsernamePasswordException;
import com.checkout.server.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final TokenService tokenService;

    @Transactional
    public String authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new InvalidUsernamePasswordException();
        }
        UserEntity user = userService.findByUserName(username);
        String token = jwtTokenProvider.generateToken(user.getUsername());
        tokenService.updateUserToken(user, token);
        return token;
    }
}

