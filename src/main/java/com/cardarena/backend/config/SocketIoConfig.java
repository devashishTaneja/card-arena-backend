package com.cardarena.backend.config;

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
import java.util.HashMap;

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

    @Bean
    Emitter listen(SocketIoServer socketIoServer, GameService gameService) {
        SocketIoNamespace namespace = socketIoServer.namespace("/");
        return namespace.on("connection", args -> {
            final SocketIoSocket socket = (SocketIoSocket) args[0];
            log.info("Connected {}", socket.getId());

            // Add a listener for the "join" event on the socket
            socket.on("join", args1 -> {
                try {
                    JSONObject jsonObject = (JSONObject) args1[0];
                    String room = jsonObject.getString("room");
                    String name = jsonObject.getString("name");
                    socket.joinRoom(room);
                    Game game = gameService.findOrInitializeGame(room);
                    String playerId = socket.getId();
                    game = gameService.addPlayer(game, new Player(playerId, name));
                    broadcastGameState(namespace, game, gameService);
                    log.info("Joined room {}", room);
                } catch (Exception e) {
                    log.error("Error joining room", e);
                    socket.send("error", e.getMessage());
                }
            });

            // Add a listener for the "join" event on the socket
            socket.on("startGame", args1 -> {
                try {
                    JSONObject jsonObject = (JSONObject) args1[0];
                    String room = jsonObject.getString("room");
                    Game game = gameService.findOrInitializeGame(room);
                    game = gameService.startGame(game);
                    broadcastGameState(namespace, game, gameService);
                    log.info("Started game {}", room);
                } catch (Exception e) {
                    log.error("Error starting game", e);
                    socket.send("error", e.getMessage());
                }
            });
        });
    }

    private void broadcastGameState(SocketIoNamespace namespace, Game game, GameService gameService) {
        String room = game.getId();
        game.getPlayers().forEach(
            player -> Arrays.stream(namespace.getAdapter().listClients(room))
                .forEach(
                    playerSocket -> playerSocket.send("stateUpdate", gameService.getGameState(game, playerSocket.getId()))
                )
        );
    }
}
