package com.cardarena.backend.service;

import com.cardarena.backend.constants.GameConstants;
import com.cardarena.backend.exception.GameAlreadyStartedException;
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
    private final GameConstants gameConstants = new GameConstants();

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
                .isSetFinished(false)
                .gameStatus(GameStatus.WAITING_FOR_PLAYERS)
                .build();
            gameRepository.save(game);
        }
        return game;
    }

    public Game addPlayer(Game game, Player player) {
        if(game.getGameStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new GameAlreadyStartedException();
        }
        List<Player> currentPlayers = game.getPlayers();
        if(currentPlayers == null){
            currentPlayers = new ArrayList<>();
            game.setOwnerId(player.getSessionId());
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
        return gameCopy.toString();
    }

    public Game startGame(Game game) {
        game.setCurrSetNumber(0);
        game.setLastSetFirstChance(-1);
        game.setScorecard(new ArrayList<>());
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
        game.setLastRoundWinner(game.getChance());
        game.getScorecard().add(new Scorecard(numOfPlayers));
        distributeCards(game, game.getCurrSetNumber());
        game.setGameStatus(GameStatus.CALLING_HANDS);
    }

    public Game call(Game game, String playerId, Integer handsCalled) {
        if(playerId==null || !playerId.equals(game.getPlayers().get(game.getChance()).getSessionId()))
            throw new InvalidTurnException();
        game.getScorecard().get(game.getCurrSetNumber()-1).getHandsCalled().set(game.getChance(), handsCalled);
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
        game.setLastRoundWinner(game.getChance());
        game.setTable(new Table());
        game.getTable().setCardsOnDisplay(new ArrayList<>());
    }

    public Game playCard(Game game,String PlayerId, Card card){
        if(PlayerId==null || !PlayerId.equals(game.getPlayers().get(game.getChance()).getSessionId())) {
            throw new InvalidTurnException();
        }
        game.getPlayers().get(game.getChance()).getCards().removeIf(car->card.getRank().equals(car.getRank()) && card.getSuit().equals(car.getSuit()));
        game.getTable().getCardsOnDisplay().add(card);
        game.setChance((game.getChance()+1)%game.getPlayers().size());
        if(game.getTable().getCardsOnDisplay().size()==game.getPlayers().size()){
            updateHands(game);
            log.info("Round Finished!");
        }
        gameRepository.save(game);
        return game;
    }

    public Game nextSetOrRound(Game game) {
        game.setGameStatus(GameStatus.PLAYING);
        game.getTable().getCardsOnDisplay().clear();
        if(game.getPlayers().get(0).getCards().isEmpty()){
            startNextSet(game);
        }
        else {
            StartNextRound(game);
        }
        gameRepository.save(game);
        return game;
    }

    private void updateHands(Game game){
        List<Card> cardsOnDisplay = game.getTable().getCardsOnDisplay();
        int chance = game.getLastRoundWinner();
        int n = game.getPlayers().size();
        Card maxCard = cardsOnDisplay.get(0);
        int maxCardPlayerId = 0;
        for(int i = 1; i<n; i++) {
            if(!maxCard.compareTo(cardsOnDisplay.get(i), Suit.valueOf(gameConstants.TRUMP_SUIT))) {
                maxCard = cardsOnDisplay.get(i);
                maxCardPlayerId = i;
            }
        }
        maxCardPlayerId= (maxCardPlayerId + chance) % n;
        game.getScorecard().get(game.getCurrSetNumber()-1).getHandsWon().set(maxCardPlayerId,game.getScorecard().get(game.getCurrSetNumber()-1).getHandsWon().get(maxCardPlayerId)+1);
        if(game.getPlayers().get(0).getCards().isEmpty()) updateScorecards(game);
        game.setChance(maxCardPlayerId);
        game.setGameStatus(GameStatus.DECLARE_WINNER);
        log.error("Player {} won the hand with card {}! - Cards {}", maxCardPlayerId, maxCard, cardsOnDisplay) ;
    }

    private void updateScorecards(Game game){
        Scorecard scorecard = game.getScorecard().get(game.getCurrSetNumber()-1);
        List<Integer> handsCalled = scorecard.getHandsCalled();
        List<Integer> handsWon = scorecard.getHandsWon();
        int n = game.numberOfPlayers();
        for(int i=0; i<n; i++) {
            if(handsCalled.get(i) == 0 && handsWon.get(i) == 0){
                scorecard.getScores().set(i,game.getCurrSetNumber());
            } else if(Objects.equals(handsCalled.get(i), handsWon.get(i))) {
                scorecard.getScores().set(i, handsCalled.get(i) * 2);
            } else {
                scorecard.getScores().set(i, -Math.abs(handsCalled.get(i) - handsWon.get(i)));
            }
        }
        List<Integer> totalScores = game.getTotalScores();
        if(totalScores == null) totalScores = new ArrayList<>(Collections.nCopies(n, 0));
        for(int i=0; i<game.numberOfPlayers(); i++) {
            totalScores.set(i, totalScores.get(i) + scorecard.getScores().get(i));
        }
        game.setTotalScores(totalScores);
    }

    private void distributeCards(Game game, int numOfCards){
        int totalCardsToBeDistributed = numOfCards * game.numberOfPlayers();
        List<Card> cards=new Deck().getCards();
        for(int i=0; i<totalCardsToBeDistributed; i++){
            int selectedCardIndex = random.nextInt(cards.size());
            Card card = cards.get(selectedCardIndex);
            game.getPlayers().get(i % game.numberOfPlayers()).getCards().add(card);
            cards.remove(selectedCardIndex);
        }
    }
}
