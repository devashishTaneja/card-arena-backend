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
                    namespace.broadcast(room, "stateUpdate", gameService.getGameState(game, playerId).toString());
                    log.info("Joined room {}", room);
                } catch (Exception e) {
                    log.error("Error joining room", e);
                    socket.send("error", e.getMessage());
                }
            });
        });
    }
}
