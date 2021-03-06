package it.polimi.ingsw.serverTest.modelTest.cardsTest.objCardTest;

import it.polimi.ingsw.server.model.cards.objective.cards.SetObj;
import it.polimi.ingsw.server.model.board.Colour;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.Schema;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static it.polimi.ingsw.server.model.builders.SchemaBuilder.buildSchema;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SetCTest {
    private Schema s;

    private void insertDice(){
        s = buildSchema(1);
        Dice d1 = new Dice(Colour.ANSI_GREEN, 1);
        Dice d2 = new Dice(Colour.ANSI_RED, 2);
        Dice d3 = new Dice(Colour.ANSI_YELLOW, 3);
        Dice d4 = new Dice(Colour.ANSI_BLUE, 4);
        Dice d5 = new Dice(Colour.ANSI_PURPLE, 5);
        Dice d6 = new Dice(Colour.ANSI_RED, 6);
        Dice d7 = new Dice(Colour.ANSI_PURPLE, 1);
        Dice d8 = new Dice(Colour.ANSI_YELLOW, 2);
        Dice d9 = new Dice(Colour.ANSI_GREEN, 3);
        Dice d10 = new Dice(Colour.ANSI_BLUE, 4);
        Dice d11 = new Dice(Colour.ANSI_GREEN, 5);
        Dice d12 = new Dice(Colour.ANSI_BLUE, 6);

        s.silentInsertDice(0, 0, d1);
        s.silentInsertDice(0, 1, d2);
        s.silentInsertDice(0, 2, d3);
        s.silentInsertDice(0, 3, d4);
        s.silentInsertDice(0, 4, d5);
        s.silentInsertDice(2, 0, d6);
        s.silentInsertDice(2, 1, d7);
        s.silentInsertDice(2, 2, d8);
        s.silentInsertDice(2, 3, d9);
        s.silentInsertDice(2, 4, d10);
        s.silentInsertDice(3, 4, d11);
        s.silentInsertDice(3, 2, d12);
    }

    @Test
    void score_correct_colour() {
        insertDice();
        SetObj card = new SetObj("name", 4);

        assertEquals(8, card.scoreCard(s), "correct computation");
    }

    @Test
    void score_correct_number() {
        insertDice();
        SetObj card = new SetObj("name", 5);

        assertEquals(10, card.scoreCard(s), "correct computation");
    }

    @Test
    void score_null(){
        s = buildSchema(1);
        SetObj card = new SetObj("name", 5);

        assertEquals(0, card.scoreCard(s), "result is 0");
    }
}
