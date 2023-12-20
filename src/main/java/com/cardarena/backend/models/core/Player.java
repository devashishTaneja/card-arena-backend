package com.cardarena.backend.models.core;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Player {
    public Integer id;

    public String name;

    public List<Card> cards;
}
