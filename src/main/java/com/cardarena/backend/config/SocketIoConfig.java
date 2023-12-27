package com.cardarena.backend.config;

import com.cardarena.backend.constants.GameConstants;
import com.cardarena.backend.models.core.Game;
import com.cardarena.backend.models.core.Player;
import com.cardarena.backend.service.GameService;
import io.socket.engineio.server.Emitter;
import io.socket.engineio.server.EngineIoServer;
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
        return new EngineIoServer();
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
                    socket.joinRoom(gameId);
                    Game game = gameService.findOrInitializeGame(gameId);
                    String playerId = socket.getId();
                    game = gameService.addPlayer(game, new Player(playerId, playerName));
                    broadcastGameState(namespace, game, gameService);
                    log.info("Joined game {}", gameId);
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
                    Game game = gameService.findOrInitializeGame(gameId);
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
                    Game game = gameService.findOrInitializeGame(gameId);
                    game = gameService.call(game, socket.getId(), handsCalled);
                    broadcastGameState(namespace, game, gameService);
                    log.info("Started game {}", gameId);
                } catch (Exception e) {
                    log.error("Error starting game", e);
                    socket.send(gameConstants.ERROR, e.getMessage());
                }
            });
        });
    }

    private void broadcastGameState(SocketIoNamespace namespace, Game game, GameService gameService) {
        String room = game.getId();
        Arrays.stream(namespace.getAdapter().listClients(room)).forEach(
            socket -> socket.send(gameConstants.STATE_UPDATE, gameService.getGameState(game, socket.getId()))
        );
    }
}
