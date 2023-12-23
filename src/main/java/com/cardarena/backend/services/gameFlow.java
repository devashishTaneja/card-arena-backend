package com.cardarena.backend.services;

import com.cardarena.backend.models.core.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Component
@Service
@Slf4j
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
                .isGameFinished(false)
                .isSetFinished(false)
                .build();
    }

    public void joinPlayer(Game game,String name){
        Player player = Player.builder()
                .id(game.getPlayers().size())
                .name(name)
                .cards(new ArrayList<>())
                .build();
        game.getPlayers().add(player);
    }

    public void startGame(Game game){
        game.setCurrSetNumber(0);
        game.setLastSetFirstChance(-1);
        game.setScorecard(new Scorecard());
        game.getScorecard().setScores(new HashMap<>());
        startNextSet(game);
    }

    public void startNextSet(Game game) {
        if((game.getCurrSetNumber()+1)*game.getPlayers().size()>game.getDeck().getCards().size()){
            game.setGameFinished(true);
            log.info("Game Finished!");
            return;
        }
        log.info("Starting Set: "+game.getCurrSetNumber()+",distributing "+(game.getCurrSetNumber()+1)+" cards to each player");
        game.setChance((game.getLastSetFirstChance()+1)%game.getPlayers().size());
        game.setLastSetFirstChance(game.getChance());
        game.setCurrSetNumber(game.getCurrSetNumber()+1);
        distributeCards(game, game.getCurrSetNumber());
    }

    public void startCall(Game game){
        log.info("Starting Calls with player :"+game.getChance());
    }

    public void call(Game game, Integer handsCalled) {
        game.getScorecard().getHandsCalled().put(game.getChance(), handsCalled);
        log.info("Player " + game.getChance() + " called " + handsCalled + " hands!");
        game.setChance((game.getChance() + 1) % game.getPlayers().size());
        if (game.getChance() == game.getLastSetFirstChance()) {
            startRound(game);
            game.setSetFinished(false);
            log.info("Starting Round!");
        }
    }

    public void updateHands(Game game){
        Card maxCard = game.getTable().getCardsOnDisplay().get(0);
        int maxCardPlayerId = game.getChance();
        for(int i = 1; i<game.getTable().getCardsOnDisplay().size(); i++){
            if(game.getTable().getCardsOnDisplay().get(i).getRank().ordinal()>maxCard.getRank().ordinal()){
                maxCard = game.getTable().getCardsOnDisplay().get(i);
                maxCardPlayerId = (game.getChance()+i)%game.getPlayers().size();
            }
        }
        game.getScorecard().getHandsWon().put(maxCardPlayerId,game.getScorecard().getHandsWon().get(maxCardPlayerId)+1);
        log.info("Player "+maxCardPlayerId+" won the hand!");
        game.getTable().getCardsOnDisplay().clear();
    }

    public void startRound(Game game){
        game.setTable(new Table());
        game.getTable().setCardsOnDisplay(new ArrayList<>());
        game.getTable().setHiddenCards(new ArrayList<>());
    }

    public void playRound(Game game, Card card){
        game.getTable().getCardsOnDisplay().add(card);
        game.setChance((game.getChance()+1)%game.getPlayers().size());
        game.getPlayers().get(game.getChance()).getCards().remove(card);
        if(game.getTable().getCardsOnDisplay().size()==game.getPlayers().size()){
            updateHands(game);
            log.info("Round Finished!");
        }
    }

    public void distributeCards(Game game, int numOfCards){
        int totalCardsToBeDistributed = numOfCards * game.getPlayers().size();
        for(int i = 0; i<totalCardsToBeDistributed; i++){
            List<Card> cards=game.getDeck().getCards();
            Random random = new Random();
            Card card = cards.get(random.nextInt(cards.size()));
            game.getPlayers().get(i % game.getPlayers().size()).getCards().add(card);
        }
    }

    public Card drawCardAtRandom(Deck deck){
        List<Card> cards=deck.getCards();
        Random random = new Random();
        return cards.get(random.nextInt(cards.size()));
    }
}
