package com.cardarena.backend.models.core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Player {
    public String id;
    public String sessionId;
    public String name;
    public List<Card> cards;

    public Player(String id, String sessionId, String name) {
        this.id = id;
        this.sessionId = sessionId;
        this.name = name;
        this.cards = new ArrayList<>();
    }
    
    public Player(Player player, Boolean hideCards) {
        this.id = player.getId();
        this.sessionId = player.getSessionId();
        this.name = player.getName();
        this.cards = hideCards ? new ArrayList<>() : player.getCards();
    }
}
