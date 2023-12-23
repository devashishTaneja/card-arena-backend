package com.cardarena.backend.service;

import io.socket.socketio.server.SocketIoSocket;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClientHandler {

    Map<String, List<SocketIoSocket>> clientMap;

    ClientHandler() {
        clientMap = new HashMap<>();
    }

    public void addClient(String room, SocketIoSocket socket) {
        List<SocketIoSocket> sockets = clientMap.getOrDefault(room, new ArrayList<>());
        sockets.add(socket);
        clientMap.put(room, sockets);
    }

    public Map<String, List<SocketIoSocket>> getClientMap() {
        return clientMap;
    }

}
