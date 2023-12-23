package com.cardarena.backend.config;

//import io.socket.engineio.server.Emitter;
import com.cardarena.backend.service.ClientHandler;
import io.socket.engineio.server.Emitter;
import io.socket.engineio.server.EngineIoServer;
import io.socket.socketio.server.SocketIoNamespace;
import io.socket.socketio.server.SocketIoServer;
import io.socket.socketio.server.SocketIoSocket;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    Emitter listen(SocketIoServer socketIoServer, ClientHandler clientHandler) {
        SocketIoNamespace namespace = socketIoServer.namespace("/");
        return namespace.on("connection", args -> {
            final SocketIoSocket socket = (SocketIoSocket) args[0];
            log.info("Connected {}", socket.getId());
            socket.on("join", args1 -> {
                String room = (String) args1[0];
                socket.joinRoom(room);
                namespace.broadcast(room, "event1", socket.getId() + " joined Room");
                log.info("Joined room {}", room);
            });
        });
    }
}
