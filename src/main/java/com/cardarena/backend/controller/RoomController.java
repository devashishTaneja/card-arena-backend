package com.cardarena.backend.controller;

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

    SocketIoServer socketIoServer;
//    ClientHandler clientHandler;

    @GetMapping("/send")
    void send() {
        SocketIoNamespace namespace = socketIoServer.namespace("/");
        namespace.broadcast("room1", "event1", "asd");
    }

}
