package com.cardarena.backend.config;

import com.cardarena.backend.constants.GameConstants;
import com.cardarena.backend.models.core.*;
import com.cardarena.backend.service.GameService;
import io.socket.engineio.server.Emitter;
import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.EngineIoServerOptions;
import io.socket.socketio.server.SocketIoNamespace;
import io.socket.socketio.server.SocketIoServer;
import io.socket.socketio.server.SocketIoSocket;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Configuration
@AllArgsConstructor
public class SocketIoConfig {

    @Bean
    EngineIoServer engineIoServer() {
        EngineIoServerOptions engineIoServerOptions = EngineIoServerOptions.newFromDefault();
        engineIoServerOptions.setCorsHandlingDisabled(true);
        engineIoServerOptions.setAllowSyncPolling(false);
        return new EngineIoServer(engineIoServerOptions);
    }

    @Bean
    SocketIoServer socketIoServer(EngineIoServer engineIoServer) {
        return new SocketIoServer(engineIoServer);
    }

    private GameConstants gameConstants;

    @Bean
    Emitter listen(SocketIoServer socketIoServer, GameService gameService) {
        SocketIoNamespace namespace = socketIoServer.namespace("/");
        return namespace.on("connection", args -> {
            final SocketIoSocket socket = (SocketIoSocket) args[0];
            log.info("Connected {}", socket.getId());

            // Add a listener for the "join" event on the socket
            socket.on(gameConstants.JOIN_GAME, args1 -> {
                try {
                    JSONObject jsonObject = (JSONObject) args1[0];
                    String gameId = jsonObject.getString(gameConstants.GAME_ID);
                    String playerName = jsonObject.getString(gameConstants.PLAYER_NAME);
                    String playerSessionId = jsonObject.getString(gameConstants.PLAYER_SESSION_ID);
                    socket.joinRoom(gameId);
                    Game game = gameService.findOrInitializeGame(gameId);
                    game = gameService.addPlayer(game, new Player(socket.getId(), playerSessionId, playerName));
                    log.info("Joined game {} - player - {}", gameId, playerName);
                    broadcastGameState(namespace, game, gameService);
                } catch (Exception e) {
                    log.error("Error joining game", e);
                    socket.send(gameConstants.ERROR, e.getMessage());
                }
            });

            // Add a listener for the "join" event on the socket
            socket.on(gameConstants.START_GAME, args1 -> {
                try {
                    JSONObject jsonObject = (JSONObject) args1[0];
                    String gameId = jsonObject.getString(gameConstants.GAME_ID);
                    String playerSessionId = jsonObject.getString(gameConstants.PLAYER_SESSION_ID);
                    Game game = gameService.findOrInitializeGame(gameId);
                    game = gameService.validatePlayer(game, playerSessionId, socket.getId());
                    game = gameService.startGame(game);
                    broadcastGameState(namespace, game, gameService);
                    log.info("Started game {}", gameId);
                } catch (Exception e) {
                    log.error("Error starting game", e);
                    socket.send(gameConstants.ERROR, e.getMessage());
                }
            });

            socket.on(gameConstants.CALL_HANDS, args1 -> {
                try {
                    JSONObject jsonObject = (JSONObject) args1[0];
                    String gameId = jsonObject.getString(gameConstants.GAME_ID);
                    Integer handsCalled = jsonObject.getInt(gameConstants.HANDS_CALLED);
                    String playerSessionId = jsonObject.getString(gameConstants.PLAYER_SESSION_ID);
                    log.info("Player {} called for {} Hands with {}", playerSessionId, handsCalled,jsonObject);
                    Game game = gameService.findOrInitializeGame(gameId);
                    game = gameService.validatePlayer(game, playerSessionId, socket.getId());
                    game = gameService.call(game, playerSessionId, handsCalled);
                    broadcastGameState(namespace, game, gameService);
                } catch (Exception e) {
                    log.error("Error calling hands", e);
                    socket.send(gameConstants.ERROR, e.getMessage());
                }
            });
            socket.on(gameConstants.PLAY_CARD, args1 -> {
                try {
                    JSONObject jsonObject = (JSONObject) args1[0];
                    String gameId = jsonObject.getString(gameConstants.GAME_ID);
                    Rank cardRank = Rank.valueOf(jsonObject.getString(gameConstants.CARD_RANK));
                    Suit cardSuit = Suit.valueOf(jsonObject.getString(gameConstants.CARD_SUIT));
                    Card card = new Card(cardSuit, cardRank);
                    String playerSessionId = jsonObject.getString(gameConstants.PLAYER_SESSION_ID);
                    log.info("{} Playing card {} of {}", playerSessionId, card.rank, card.suit);
                    Game game = gameService.findOrInitializeGame(gameId);
                    game = gameService.validatePlayer(game, playerSessionId, socket.getId());
                    game = gameService.playCard(game, playerSessionId, card);
                    broadcastGameState(namespace, game, gameService);
                } catch (Exception e) {
                    log.error("Error playing card", e);
                    socket.send(gameConstants.ERROR, e.getMessage());
                }
            });

            socket.on(gameConstants.NEXT_HAND, args1 -> {
                try {
                    JSONObject jsonObject = (JSONObject) args1[0];
                    String gameId = jsonObject.getString(gameConstants.GAME_ID);
                    String playerSessionId = jsonObject.getString(gameConstants.PLAYER_SESSION_ID);
                    log.info("{} Starting next hand", playerSessionId);
                    Game game = gameService.findOrInitializeGame(gameId);
                    game = gameService.validatePlayer(game, playerSessionId, socket.getId());
                    game = gameService.nextSetOrRound(game);
                    broadcastGameState(namespace, game, gameService);
                } catch (Exception e) {
                    log.error("Error starting next hand", e);
                    socket.send(gameConstants.ERROR, e.getMessage());
                }
            });
        });
    }

    private void broadcastGameState(SocketIoNamespace namespace, Game game, GameService gameService) {
        log.info("Broadcasting game state {}", game);
        String room = game.getId();
        Arrays.stream(namespace.getAdapter().listClients(room)).forEach(
            socket -> {
                log.info("Sending game state to {}", socket.getId());
                socket.send(gameConstants.STATE_UPDATE, gameService.getGameState(game, socket.getId()));
            }
        );
    }
}
