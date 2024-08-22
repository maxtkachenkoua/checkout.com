package com.checkout.server.rest.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDto {
    @NotBlank(message = "username field is not set")
    private String username;
    @NotBlank(message = "password field is not set")
    private String password;
}
