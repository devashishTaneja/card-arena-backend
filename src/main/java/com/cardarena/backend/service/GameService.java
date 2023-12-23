package com.cardarena.backend.service;

import com.cardarena.backend.models.core.Card;
import com.cardarena.backend.models.core.Game;
import com.cardarena.backend.models.core.Player;
import com.cardarena.backend.repository.core.GameRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GameService {

    GameRepository gameRepository;

    public Game findOrInitializeGame(String roomId) {
        Game game = gameRepository.findById(roomId).orElse(null);
        if (game == null) {
            game = new Game();
            game.setId(roomId);
            gameRepository.save(game);
        }
        return game;
    }

    public Game addPlayer(Game game, Player player) {
        List<Player> currentPlayers = game.getPlayers();
        if(currentPlayers == null) currentPlayers = new ArrayList<>();
        // Return if player already exists
        if(currentPlayers.stream().anyMatch(player1 -> player1.getId().equals(player.getId()))) return game;
        currentPlayers.add(player);
        game.setPlayers(currentPlayers);
        return gameRepository.save(game);
    }

    public Game getGameState(Game game, String playerId) {
        List<Player> players = game.getPlayers();
        players = players.stream().peek(player -> {
            if (!player.getId().equals(playerId)) {
                List<Card> cards = player.getCards().stream().map(card -> {
                    card.setRank(null);
                    card.setSuit(null);
                    return card;
                }).toList();
                player.setCards(cards);
            }
        }).toList();
        game.setPlayers(players);
        return game;
    }

    public Game startGame(Game game) {
        game = distributeCards(game);
        return game;
    }

    private Game distributeCards(Game game) {
        // Todo: Distribute cards to players
        return game;
    }

}
