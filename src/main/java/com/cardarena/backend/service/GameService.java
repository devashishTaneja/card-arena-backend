package com.cardarena.backend.service;

import com.cardarena.backend.constants.GameConstants;
import com.cardarena.backend.exception.InvalidTurnException;
import com.cardarena.backend.models.core.*;
import com.cardarena.backend.repository.core.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final Random random;
    private GameConstants gameConstants;

    GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.random = new Random();
    }

    private static final int TOTAL_CARD_COUNT = 52;

    public Game findOrInitializeGame(String roomId) {
        Game game = gameRepository.findById(roomId).orElse(null);
        if (game == null) {
            game = Game
                .builder()
                .id(roomId)
                .deck(new Deck())
                .isSetFinished(false)
                .gameStatus(GameStatus.WAITING_FOR_PLAYERS)
                .build();
            gameRepository.save(game);
        }
        return game;
    }

    public Game addPlayer(Game game, Player player) {
        List<Player> currentPlayers = game.getPlayers();
        if(currentPlayers == null){
            currentPlayers = new ArrayList<>();
            game.setOwnerId(player.getId());
        }
        // Return if player already exists
        final boolean[] playerExist = {false};
        currentPlayers.forEach(curPlayer -> {
            if(curPlayer.getSessionId().equals(player.getSessionId())){
                playerExist[0] = true;
                curPlayer.setId(player.getId());
            }
        });
        if(!playerExist[0]){
            currentPlayers.add(player);
            game.setPlayers(currentPlayers);
        }
        return gameRepository.save(game);
    }

    public Game validatePlayer(Game game, String playerSessionId, String socketId) {
        game.getPlayers()
            .forEach(player -> {
                if (player.getSessionId().equals(playerSessionId) && !Objects.equals(player.getId(), socketId)) {
                    player.setId(socketId);
                }
            });
        return game;
    }

    public String getGameState(Game game, String playerId) {
        Game gameCopy = new Game(game);
        List<Player> players = game.getPlayers();
        players = players.stream().map(player -> new Player(player, !player.getId().equals(playerId))).toList();
        gameCopy.setPlayers(players);
        gameCopy.setDeck(null);
        return gameCopy.toString();
    }

    public Game startGame(Game game) {
        game.setCurrSetNumber(0);
        game.setLastSetFirstChance(-1);
        game.setScorecard(new Scorecard());
        game.setDeck(new Deck());
        game.getPlayers().forEach(player -> player.setCards(new ArrayList<>()));
        game.setGameStatus(GameStatus.STARTING_SET);
        startNextSet(game);
        gameRepository.save(game);
        return game;
    }

    private void startNextSet(Game game) {
        int numOfPlayers = game.numberOfPlayers();
        game.setCurrSetNumber(game.getCurrSetNumber()+1);
        if(game.getCurrSetNumber()*numOfPlayers > TOTAL_CARD_COUNT ) {
            game.setGameStatus(GameStatus.FINISHED);
            log.info("Game Finished!");
            return;
        }
        log.info("Starting Set: " + game.getCurrSetNumber() + ",distributing "  + game.getCurrSetNumber() + " cards to each player");
        game.setChance((game.getLastSetFirstChance()+1)%numOfPlayers);
        game.setLastSetFirstChance(game.getChance());
        distributeCards(game, game.getCurrSetNumber());
        game.setGameStatus(GameStatus.CALLING_HANDS);
    }

    public Game call(Game game, String playerId, Integer handsCalled) {
        if(playerId==null || !playerId.equals(game.getPlayers().get(game.getChance()).getId()))
            throw new InvalidTurnException();
        game.getScorecard().getHandsCalled().put(game.getChance(), handsCalled);
        log.info("Player {} called {} hands!", game.getChance(), handsCalled);
        game.setChance((game.getChance() + 1) % game.numberOfPlayers());
        if(Objects.equals(game.getChance(), game.getLastSetFirstChance())) {
            StartNextRound(game);
            game.setGameStatus(GameStatus.PLAYING);
            log.info("Starting Round!");
        }
        gameRepository.save(game);
        return game;
    }

    private void StartNextRound(Game game){
        game.setTable(new Table());
        game.getTable().setCardsOnDisplay(new ArrayList<>());
        game.getTable().setHiddenCards(new ArrayList<>());
    }

    public Game playCard(Game game,String PlayerId, Card card){
        if(PlayerId==null || !PlayerId.equals(game.getPlayers().get(game.getChance()).getId())) {
            throw new InvalidTurnException();
        }
        game.getTable().getCardsOnDisplay().add(card);
        game.setChance((game.getChance()+1)%game.getPlayers().size());
        game.getPlayers().get(game.getChance()).getCards().remove(card);
        if(game.getTable().getCardsOnDisplay().size()==game.getPlayers().size()){
            updateHands(game);
            log.info("Round Finished!");
            if(game.getPlayers().get(0).getCards().isEmpty()){
                startNextSet(game);
            }
            else{
                StartNextRound(game);
            }
        }
        return game;
    }

    private void updateHands(Game game){
        List<Card> cardsOnDisplay = game.getTable().getCardsOnDisplay();
        Card maxCard = cardsOnDisplay.get(0);
        int maxCardPlayerId = game.getChance();
        for(int i = 1; i<cardsOnDisplay.size(); i++){
            if(compareCards(maxCard, cardsOnDisplay.get(i), Suit.valueOf(gameConstants.TRUMP_SUIT))){
                maxCard = cardsOnDisplay.get(i);
                maxCardPlayerId = (game.getChance()+i)%game.getPlayers().size();
            }
        }
        game.getScorecard().getHandsWon().put(maxCardPlayerId,game.getScorecard().getHandsWon().get(maxCardPlayerId)+1);
        game.setChance(maxCardPlayerId);
        log.info("Player "+maxCardPlayerId+" won the hand!");
        game.getTable().getCardsOnDisplay().clear();
    }

    private boolean compareCards(Card card1, Card card2, Suit trumpSuit){
        if(card1.getSuit().equals(card2.getSuit())){
            return card1.getRank().ordinal()<card2.getRank().ordinal();
        }
        else return card2.getSuit().equals(trumpSuit);
    }

    private void distributeCards(Game game, int numOfCards){
        int totalCardsToBeDistributed = numOfCards * game.numberOfPlayers();
        List<Card> cards=game.getDeck().getCards();
        for(int i=0; i<totalCardsToBeDistributed; i++){
            Card card = cards.get(random.nextInt(cards.size()+1)-1);
            game.getPlayers().get(i % game.numberOfPlayers()).getCards().add(card);
            cards.remove(card);
        }
        game.setDeck(new Deck(cards));
    }
}
