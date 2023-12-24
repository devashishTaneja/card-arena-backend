package com.cardarena.backend.models.core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class Scorecard {
    public HashMap<Integer,Integer> handsCalled;
    public HashMap<Integer,Integer> handsWon;
    public HashMap<Integer,Integer> scores;
}
