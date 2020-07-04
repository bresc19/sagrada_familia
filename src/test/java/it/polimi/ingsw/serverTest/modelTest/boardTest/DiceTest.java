package it.polimi.ingsw.serverTest.modelTest.boardTest;

import it.polimi.ingsw.server.exception.ChangeDiceValueException;
import it.polimi.ingsw.server.model.board.Colour;
import it.polimi.ingsw.server.model.board.Dice;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


class DiceTest {
    private Dice dice1;
    private Dice dice2;
    private Dice dice3;

    private void testInit(){
        dice1 = new Dice(Colour.ANSI_GREEN, 4);
        dice2 = new Dice(Colour.ANSI_RED, 6);
        dice3 = new Dice(Colour.ANSI_PURPLE, 1);
    }

    private boolean CorrectDice(int n) {
        return n > 0 && n < 7;
    }


    @Test
    void correctDiceFormat(){
        Dice d =new Dice(Colour.ANSI_GREEN, 6);
        assertTrue("Dice's number is a right value", CorrectDice(d.getValue()));
    }

    @Test
    void wrongDiceFormat(){
        Dice d =new Dice(Colour.ANSI_GREEN, 10);
        assertFalse("Number is wrong. " + d.getValue() + "is not a dice's value", CorrectDice(d.getValue()));
    }

    @Test
    void incrementValue(){
        testInit();
        try {
            dice1.incrementValue();
            assertEquals(5,dice1.getValue());
        } catch (ChangeDiceValueException e) {
            e.getMessage();
        }
        assertThrows(ChangeDiceValueException.class, () -> dice2.incrementValue());
    }

    @Test
    void decrementValue(){
        testInit();
        try {
            dice1.decrementValue();
            assertEquals(3,dice1.getValue());
        } catch (ChangeDiceValueException e) {
            e.getMessage();
        }
        assertThrows(ChangeDiceValueException.class, () -> dice3.decrementValue());
    }

    @Test
    void rollDice(){
        testInit();
        Colour oldColour = dice1.getColour();
        dice1.rollDice();
        assertEquals(oldColour,dice1.getColour());
    }
}
