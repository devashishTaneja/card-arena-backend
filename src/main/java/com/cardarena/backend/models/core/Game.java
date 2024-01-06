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
    public Deck deck;
    public Suit trump;
    public Integer chance;
    public Integer currSetNumber;
    public Scorecard scorecard;
    public Table table;
    public Integer lastSetFirstChance;
    public boolean isSetFinished;
    public GameStatus gameStatus;

    public Game(Game game) {
        this.id = game.id;
        this.ownerId = game.ownerId;
        this.numOfCards = game.numOfCards;
        this.players = game.players;
        this.deck = game.deck;
        this.chance = game.chance;
        this.currSetNumber = game.currSetNumber;
        this.scorecard = game.scorecard;
        this.table = game.table;
        this.lastSetFirstChance = game.lastSetFirstChance;
        this.isSetFinished = game.isSetFinished;
        this.gameStatus = game.gameStatus;
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

