package com.cardarena.backend.models.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Card {
    public Suit suit;
    public Rank rank;
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }
}