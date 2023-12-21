package com.cardarena.backend.models.core;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class Player {
    public String id;
    public String name;
    public List<Card> cards;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.cards = new ArrayList<>();
    }
}
