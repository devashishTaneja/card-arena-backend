package com.cardarena.backend.models.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    public String id;
    public String ownerId;
    public Integer numOfCards;
    public List<Player> players;
    public Suit trump;
    public Integer chance;
    public Integer currSetNumber;
    public List<Scorecard> scorecard;
    public Table table;
    // First person to call hands in this set
    public Integer lastSetFirstChance;
    // Winner of last round(hand) or Person who starts this round(hand)
    public Integer lastRoundWinner;
    public boolean isSetFinished;
    public GameStatus gameStatus;

    public Game(Game game) {
        this.id = game.id;
        this.ownerId = game.ownerId;
        this.numOfCards = game.numOfCards;
        this.players = game.players;
        this.chance = game.chance;
        this.currSetNumber = game.currSetNumber;
        this.trump = game.trump;
        this.table = game.table;
        this.lastSetFirstChance = game.lastSetFirstChance;
        this.isSetFinished = game.isSetFinished;
        this.gameStatus = game.gameStatus;
        this.lastRoundWinner = game.lastRoundWinner;
        this.scorecard = game.scorecard;
    }

    public Integer numberOfPlayers(){
        return players.size();
    }

    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

