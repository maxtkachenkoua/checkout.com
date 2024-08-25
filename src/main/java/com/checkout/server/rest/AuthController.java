package com.checkout.server.rest;

import com.checkout.server.rest.dto.auth.AuthRequestDto;
import com.checkout.server.rest.dto.auth.AuthResponseDto;
import com.checkout.server.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) throws Exception {
        return ResponseEntity.ok(AuthResponseDto.builder()
                .token(authService.authenticate(request.getUsername(), request.getPassword()))
                .build());
    }
}

