package com.checkout.server.rest;

import com.checkout.server.rest.dto.auth.AuthRequestDto;
import com.checkout.server.rest.dto.auth.AuthResponseDto;
import com.checkout.server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "User login", description = "Authenticate user with username and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) throws Exception {
        return ResponseEntity.ok(AuthResponseDto.builder()
                .token(authService.authenticate(request.getUsername(), request.getPassword()))
                .build());
    }
}

