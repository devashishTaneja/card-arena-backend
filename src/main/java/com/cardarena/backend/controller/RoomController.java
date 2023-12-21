package com.cardarena.backend.controller;

import io.socket.engineio.server.EngineIoServer;
import io.socket.socketio.server.SocketIoNamespace;
import io.socket.socketio.server.SocketIoServer;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@AllArgsConstructor
public class RoomController {

    EngineIoServer mEngineIoServer;

    @GetMapping("/send")
    void send() {
        socketIoServer.namespace("/")
                .
        socket.send(new Packet<>(Packet.MESSAGE, "foo"));

    }

}


