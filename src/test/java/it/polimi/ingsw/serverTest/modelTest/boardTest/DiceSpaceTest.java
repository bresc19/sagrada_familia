package it.polimi.ingsw.serverTest.modelTest.boardTest;

import it.polimi.ingsw.server.exception.RemoveDiceException;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.Colour;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.DiceSpace;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DiceSpaceTest {
    private Dice d1 = new Dice(Colour.ANSI_PURPLE,3);
    private Dice d2 = new Dice(Colour.ANSI_YELLOW,4);
    private Dice d3 = new Dice(Colour.ANSI_PURPLE,5);
    private Dice d4 = new Dice(Colour.ANSI_RED,5);
    private ArrayList<Dice> dices = new ArrayList<>();
    private DiceSpace diceSpace = new DiceSpace(new Board(new ArrayList<>()));

    private void testInit(){
        dices.add(d1);
        dices.add(d2);
        dices.add(d3);
        diceSpace.setDices(dices);
    }

    @Test
    void setDiceSpace(){
        testInit();
        assertSame(dices,diceSpace.getListDice());
    }

    @Test
    void insertDice() {
        testInit();
        diceSpace.insertDice(d4);
        assertTrue(diceSpace.getListDice().contains(d4));
        assertEquals(4, diceSpace.getListDice().size());
    }

    @Test
    void removeDice(){
        testInit();
        try {
            Dice dice = diceSpace.removeDice(0);
            assertEquals(2,diceSpace.getListDice().size());
            assertSame(dice,d1);
        } catch (RemoveDiceException e) {
            e.printStackTrace();
        }
        assertThrows(RemoveDiceException.class,() -> diceSpace.removeDice(4));
        assertThrows(RemoveDiceException.class,() -> diceSpace.removeDice(-1));
    }

    @Test
    void getDice(){
        testInit();
        try {
            Dice dice = diceSpace.getDice(0,"player 1");
            assertEquals(3,diceSpace.getListDice().size());
            assertSame(dice,d1);
        } catch (RemoveDiceException e) {
            e.printStackTrace();
        }
        assertThrows(RemoveDiceException.class,() -> diceSpace.getDice(4,"player 1"));
        assertThrows(RemoveDiceException.class,() -> diceSpace.getDice(-1,"player 1"));
    }

    @Test
    void rollDices(){
        testInit();
        diceSpace.rollDices();
        for(int i = 0; i<dices.size(); i++)
            assertEquals(dices.get(i).getColour(),diceSpace.getListDice().get(i).getColour());
        assertEquals(dices.size(),diceSpace.getListDice().size());
    }
}
