package com.cardarena.backend.services;

import com.cardarena.backend.models.core.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Component
@Service
public class gameFlow {
    public Game distributeCards(Game game){
        int totalPlayers = game.getNumOfPlayers();
        int totalCardsToBeDistributed = totalPlayers * game.getNumOfPlayers();
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
