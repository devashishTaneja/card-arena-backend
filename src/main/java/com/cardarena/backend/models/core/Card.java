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

    // Returns true if greater than card
    public boolean compareTo(Card card, Suit trumpSuit) {
        if (this.suit == card.suit) {
            return this.rank.ordinal() < card.rank.ordinal();
        } else return !card.suit.equals(trumpSuit);
    }

    @Override
    public String toString() {
        return "Card{" +
                "suit=" + suit +
                ", rank=" + rank +
                '}';
    }
}