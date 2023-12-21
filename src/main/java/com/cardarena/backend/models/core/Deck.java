package com.cardarena.backend.models.core;

import jakarta.servlet.http.PushBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
public class Deck {
    public List<Card> cards;

    public Deck(){
        List<Card> cards = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        this.cards=cards;
    }
}
