package com.checkout.server.exception;

public class InvalidUsernamePasswordException extends Exception {
    public InvalidUsernamePasswordException() {
        super("Incorrect username or password");
    }
}
