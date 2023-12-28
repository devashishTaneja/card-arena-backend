package com.cardarena.backend.models.core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
}
