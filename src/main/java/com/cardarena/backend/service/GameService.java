package com.cardarena.backend.service;

import com.cardarena.backend.exception.InvalidTurnException;
import com.cardarena.backend.models.core.*;
import com.cardarena.backend.repository.core.GameRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final Random random;

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
                .isGameFinished(false)
                .isSetFinished(false)
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
        if(currentPlayers.stream().anyMatch(player1 -> player1.getId().equals(player.getId()))) return game;
        currentPlayers.add(player);
        game.setPlayers(currentPlayers);
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
        List<Player> players = game.getPlayers();
        players = players.stream().map(player -> {
            if (!player.getId().equals(playerId)) {
                List<Card> cards = player.getCards().stream().map(card -> {
                    card.setRank(null);
                    card.setSuit(null);
                    return card;
                }).toList();
                player.setCards(cards);
            }
            return player;
        }).toList();
        game.setPlayers(players);
        game.setDeck(null);
        return game.toString();
    }

    public Game startGame(Game game) {
        game.setCurrSetNumber(0);
        game.setLastSetFirstChance(-1);
        game.setScorecard(new Scorecard());
        game.setDeck(new Deck());
        game.getPlayers().forEach(player -> player.setCards(new ArrayList<>()));
        startNextSet(game);
        gameRepository.save(game);
        return game;
    }

    public Game call(Game game, String playerId,Integer handsCalled) {
        if(playerId==null || !playerId.equals(game.getPlayers().get(game.getChance()).getId()))
            throw new InvalidTurnException();
        game.getScorecard().getHandsCalled().put(game.getChance(), handsCalled);
        log.info("Player {} called {} hands!", game.getChance(), handsCalled);
        game.setChance((game.getChance() + 1) % game.numberOfPlayers());
        if(Objects.equals(game.getChance(), game.getLastSetFirstChance())) {
            startRound(game);
            game.setSetFinished(false);
            log.info("Starting Round!");
        }
        gameRepository.save(game);
        return game;
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

    private void updateHands(Game game){
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

    private void startRound(Game game){
        game.setTable(new Table());
        game.getTable().setCardsOnDisplay(new ArrayList<>());
        game.getTable().setHiddenCards(new ArrayList<>());
    }

    private void startNextSet(Game game) {
        int numOfPlayers = game.numberOfPlayers();
        game.setCurrSetNumber(game.getCurrSetNumber()+1);
        // Todo: Inform UI when game is finished
        if(game.getCurrSetNumber()*numOfPlayers > TOTAL_CARD_COUNT ) {
            game.setGameFinished(true);
            log.info("Game Finished!");
            return;
        }
        log.info("Starting Set: "+game.getCurrSetNumber()+",distributing "+game.getCurrSetNumber()+" cards to each player");
        game.setChance((game.getLastSetFirstChance()+1)%numOfPlayers);
        game.setLastSetFirstChance(game.getChance());
        distributeCards(game, game.getCurrSetNumber());
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
