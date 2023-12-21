package com.cardarena.backend.models.core;

import jakarta.servlet.http.PushBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Deck {
    public List<Card> cards;
}
