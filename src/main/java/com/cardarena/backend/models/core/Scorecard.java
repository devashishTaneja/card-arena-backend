package com.cardarena.backend.models.core;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class Scorecard {
    public HashMap<Integer,Integer> handsCalled;
    public HashMap<Integer,Integer> handsWon;
    public HashMap<Integer,Integer> scores;
}
