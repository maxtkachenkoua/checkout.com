package com.checkout.server.exception;


import com.checkout.server.exception.model.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

import static com.checkout.server.exception.model.ErrorMessages.REQUEST_BODY_MISSING_OR_INVALID;
import static com.checkout.server.exception.model.ErrorMessages.REQUIRED_REQUEST_PART_MISSING;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetails> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(ErrorDetails.builder()
                .timestamp(System.currentTimeMillis())
                .message(REQUEST_BODY_MISSING_OR_INVALID)
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorDetails> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        return new ResponseEntity<>(ErrorDetails.builder()
                .timestamp(System.currentTimeMillis())
                .message(REQUIRED_REQUEST_PART_MISSING)
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUsernameNotFoundException(UsernameNotFoundException e) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(System.currentTimeMillis())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidUsernamePasswordException.class)
    public ResponseEntity<ErrorDetails> handleInvalidUsernamePasswordException(InvalidUsernamePasswordException e) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(System.currentTimeMillis())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorDetails> handlePaymentException(PaymentException e) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(System.currentTimeMillis())
                .message(String.join(",", e.getPaymentErrors()))
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(DuplicatedCardNumberException.class)
    public ResponseEntity<ErrorDetails> handleDuplicatedCardNumberException(DuplicatedCardNumberException e) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(System.currentTimeMillis())
                .message(e.getErrorMessage())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(System.currentTimeMillis())
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}