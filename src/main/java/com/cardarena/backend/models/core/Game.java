package com.cardarena.backend.models.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Game {
    public String id;
    public Integer ownerId;
    public Integer numOfPlayers;
    public Integer numOfCards;
    public List<Player> players;
    public Deck deck;
    public Integer chance;
    public Integer numOfRounds;
    public Scorecard scorecard;
    public Table table;
    public Integer lastRoundFirstChance;
}
