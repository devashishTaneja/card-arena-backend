package com.cardarena.backend.services;

import com.cardarena.backend.models.core.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@Service
public class gameFlow {

    public Game createGame(String GameOwnerName){
        Player player = Player.builder()
                .id(0)
                .name(GameOwnerName)
                .cards(new ArrayList<>())
                .build();
        String uniqueId = UUID.randomUUID().toString().replace("-", "");

        return Game.builder()
                .id(uniqueId.substring(0,12))
                .ownerId(0)
                .players(List.of(player))
                .deck(new Deck())
                .chance(player.getId())
                .build();
    }

    public Game joinPlayer(Game game,String name){
        Player player = Player.builder()
                .id(game.getPlayers().size())
                .name(name)
                .cards(new ArrayList<>())
                .build();
        game.getPlayers().add(player);
        return game;
    }

    public Game startGame(Game game){
        game.setNumOfPlayers(game.getPlayers().size());
        distributeCards(game);
        return game;
    }

    public void distributeCards(Game game){
        int totalPlayers = game.getNumOfPlayers();
        int totalCardsToBeDistributed = totalPlayers * game.getNumOfPlayers();
        for(int i = 0; i<totalCardsToBeDistributed; i++){
            List<Card> cards=game.getDeck().getCards();
            Random random = new Random();
            Card card = cards.get(random.nextInt(cards.size()));
            game.getPlayers().get(i%totalPlayers).getCards().add(card);
        }
    }

    public Card drawCardAtRandom(Deck deck){
        List<Card> cards=deck.getCards();
        Random random = new Random();
        return cards.get(random.nextInt(cards.size()));
    }
}
