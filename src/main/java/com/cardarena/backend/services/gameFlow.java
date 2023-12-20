package com.cardarena.backend.services;

import com.cardarena.backend.models.Deck;
import com.cardarena.backend.models.Game;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class gameFlow {
    public Game distributeCards(Game game){

        return game;
    }
}
