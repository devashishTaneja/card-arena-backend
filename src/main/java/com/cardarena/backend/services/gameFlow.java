package com.cardarena.backend.services;

import com.cardarena.backend.models.core.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class gameFlow {
    public Game distributeCards(Game game){
        // Todo: @Yogesh: Check if this is the correct way to distribute cards
        // Multiplying same numbers twice
        int totalPlayers = game.getPlayers().size();
        int totalCardsToBeDistributed = totalPlayers * game.getPlayers().size();
        for(int i = 0; i<totalCardsToBeDistributed; i++){
            List<Card> cards=game.getDeck().getCards();
            Random random = new Random();
            Card card = cards.get(random.nextInt(cards.size()));
            game.getPlayers().get(i%totalPlayers).getCards().add(card);
        }
        return game;
    }

    public Card drawCardAtRandom(Deck deck){
        List<Card> cards=deck.getCards();
        Random random = new Random();
        return cards.get(random.nextInt(cards.size()));
    }
}
