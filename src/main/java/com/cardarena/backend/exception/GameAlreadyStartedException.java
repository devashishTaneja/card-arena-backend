package com.cardarena.backend.exception;

public class GameAlreadyStartedException extends RuntimeException {
    public GameAlreadyStartedException() {
        super("Game already in-progress");
    }
}
