package com.cardarena.backend.models.core;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Table {
    public List<Card> cardsOnDisplay;
    public List<Card> hiddenCards;
}
