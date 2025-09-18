package com.arquitectura.voting.exception;

public class QuorumNotReachedException extends RuntimeException {
    public QuorumNotReachedException(String message) {
        super(message);
    }
}