package com.cardarena.backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("room")
@AllArgsConstructor
public class RoomController {

    @GetMapping("health")
    ResponseEntity<String> health() {
        return ResponseEntity.ok("OK1");
    }

    @PostMapping("create")
    ResponseEntity<String> createRoom() {
        String roomName = UUID.randomUUID().toString().substring(0, 8);
        return ResponseEntity.ok(roomName);
    }

}
