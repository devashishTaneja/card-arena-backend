package com.cardarena.backend.exception;

public class InvalidTurnException extends RuntimeException {
    public InvalidTurnException() {
        super("Invalid player turn");
    }
}
