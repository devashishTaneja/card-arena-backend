package com.cardarena.backend.models.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Scorecard {
    public List<Integer> handsCalled;
    public List<Integer> handsWon;
    public List<Integer> scores;
    public Scorecard(Integer numOfPlayers){
        this.handsCalled = new ArrayList<>(Collections.nCopies(numOfPlayers, null));
        this.handsWon = new ArrayList<>(Collections.nCopies(numOfPlayers, 0));
        this.scores = new ArrayList<>(Collections.nCopies(numOfPlayers, 0));
    }
}
