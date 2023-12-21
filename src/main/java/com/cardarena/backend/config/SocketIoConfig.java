package com.cardarena.backend.config;

import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.Emitter;
import io.socket.socketio.server.SocketIoAdapter;
import io.socket.socketio.server.SocketIoNamespace;
import io.socket.socketio.server.SocketIoServer;
import io.socket.socketio.server.SocketIoSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SocketIoConfig {

    @Bean
    EngineIoServer engineIoServer() {
        return new EngineIoServer();
    }

    @Bean
    SocketIoServer socketIoServer(EngineIoServer engineIoServer) {
        return new SocketIoServer(engineIoServer);
    }

//    @Bean
//    Emitter listen(SocketIoServer socketIoServer) {
//        SocketIoNamespace namespace = socketIoServer.namespace("/");
//        return namespace.on("connection", args -> {
//            SocketIoSocket socket = (SocketIoSocket) args[0];
//            SocketIoAdapter adapter = socketIoServer.namespace("/").getAdapter();
//            adapter.add("room1", socket);
//            log.info("Connected {}", socket.getId().toString());
//        });
//    }

}
