package com.checkout.server.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DuplicatedCardNumberException extends Exception {
    private final String errorMessage;

    public DuplicatedCardNumberException(String cardNumber) {
        this.errorMessage = String.format("Card with number %s already tokenized for current user", cardNumber);
    }
}
