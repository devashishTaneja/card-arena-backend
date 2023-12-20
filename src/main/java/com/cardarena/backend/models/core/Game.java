package com.cardarena.backend.models.core;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Game {
    public Integer id;
    public Integer numOfPlayers;
    public Integer numOfCards;
    public List<Player> players;
    public Deck deck;
    public Integer chance;
    public Integer numOfRounds;
}
