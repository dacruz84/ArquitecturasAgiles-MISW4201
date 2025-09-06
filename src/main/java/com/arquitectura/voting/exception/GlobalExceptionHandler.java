package com.arquitectura.voting.exception;

import com.arquitectura.voting.exception.QuorumNotReachedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(QuorumNotReachedException.class)
    public ResponseEntity<String> handleQuorumNotReached(QuorumNotReachedException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMessage());
    }
}