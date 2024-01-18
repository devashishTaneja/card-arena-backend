package com.cardarena.backend.models.core;

public enum GameStatus {
    WAITING_FOR_PLAYERS,
    STARTING_SET,
    CALLING_HANDS,
    PLAYING,
    DECLARE_WINNER,
    FINISHED,
    ERROR
}
