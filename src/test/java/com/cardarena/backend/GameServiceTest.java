package com.cardarena.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.cardarena.backend.service.*;
import static org.junit.jupiter.api.Assertions.*;
import com.cardarena.backend.models.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GameServiceTest {
    @Test
    void testCompareCards() {
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.HEARTS, Rank.EIGHT);
        Suit trumpSuit = Suit.DIAMONDS;
        assertTrue(card1.compareTo(card2, trumpSuit));

        card1 = new Card(Suit.SPADES, Rank.TWO);
        card2 = new Card(Suit.HEARTS, Rank.EIGHT);
        assertTrue(card1.compareTo(card2, trumpSuit));

        card1 = new Card(Suit.DIAMONDS, Rank.ACE);
        card2 = new Card(Suit.HEARTS, Rank.EIGHT);
        assertTrue(card1.compareTo(card2, trumpSuit));

        card1 = new Card(Suit.HEARTS, Rank.ACE);
        card2 = new Card(Suit.DIAMONDS, Rank.TWO);
        assertFalse(card1.compareTo(card2, trumpSuit));
    }
}