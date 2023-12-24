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
    public Integer chance;
    public Integer currSetNumber;
    public Scorecard scorecard;
    public Table table;
    public Integer lastSetFirstChance;
    public boolean isGameFinished;
    public boolean isSetFinished;

    public Integer numberOfPlayers(){
        return players.size();
    }

    // Add a method to convert the object to JSON string
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
