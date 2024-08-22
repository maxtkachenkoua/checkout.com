package com.checkout.server.exception.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDetails {
    private Long timestamp;
    private String message;
    private String details;
}