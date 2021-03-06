package it.polimi.ingsw.serverTest.modelTest.cardsTest.objCardTest;

import it.polimi.ingsw.server.model.cards.objective.cards.ColumnsObj;
import it.polimi.ingsw.server.model.board.Colour;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.Schema;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static it.polimi.ingsw.server.model.builders.SchemaBuilder.buildSchema;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ColumnsCTest {

    private Schema s;

    private void insertDice() {
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


        s.silentInsertDice(0, 0, d1);
        s.silentInsertDice(1, 0, d2);
        s.silentInsertDice(2, 0, d3);
        s.silentInsertDice(3, 0, d4);
        s.silentInsertDice(0, 2, d5);
        s.silentInsertDice(1, 2, d6);
        s.silentInsertDice(2, 2, d7);
        s.silentInsertDice(3, 2, d8);
        s.silentInsertDice(3, 4, d9);
        s.silentInsertDice(3, 3, d10);
    }

    @Test
    void scoreIs5() {
        insertDice();
        ColumnsObj card = new ColumnsObj("card", 5);
        assertEquals(5, card.scoreCard(s), "score correct");
    }

    @Test
    void scoreIs4() {
        insertDice();
        ColumnsObj card = new ColumnsObj("card", 4);
        assertEquals(8, card.scoreCard(s), "score correct");
    }

    @Test
    void nullScore() {
        s = buildSchema(1);
        ColumnsObj card = new ColumnsObj("card", 4);

        assertEquals(0, card.scoreCard(s), "result is 0");
    }
}
