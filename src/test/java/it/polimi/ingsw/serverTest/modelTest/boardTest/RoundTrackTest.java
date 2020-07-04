package it.polimi.ingsw.serverTest.modelTest.boardTest;


import it.polimi.ingsw.server.exception.InsertDiceException;
import it.polimi.ingsw.server.exception.RemoveDiceException;
import it.polimi.ingsw.server.model.board.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoundTrackTest {
    private RoundTrack roundTrack = new RoundTrack(new Board(new ArrayList<>()));
    private ArrayList<Dice> listDice = new ArrayList<>();
    private Dice d1;
    private Dice d2;
    private Dice d3;

    private void setDices(){
        d1 = new Dice(Colour.ANSI_PURPLE, 3);
        d2 = new Dice(Colour.ANSI_GREEN, 2);
        d3 = new Dice(Colour.ANSI_RED, 1);
        listDice.add(d1);
        listDice.add(d2);
    }

    private void reconnectPlayer(){
        roundTrack.reconnectPlayer(new Player("player 1"));
    }

    @Test
    void insertDice(){
        setDices();
        roundTrack.insertDices(listDice, 0);
        assertEquals(listDice.size(), roundTrack.getListRounds(0).size());
        assertEquals(d1, roundTrack.getListRounds(0).get(0));
        assertEquals(d2, roundTrack.getListRounds(0).get(1));

        try {
            roundTrack.insertDice(d3,0);
            assertEquals(3, roundTrack.getListRounds(0).size());
            assertEquals(d3, roundTrack.getListRounds(0).get(2));
        } catch (InsertDiceException e) {
            e.printStackTrace();
        }
        assertThrows(InsertDiceException.class,() -> roundTrack.insertDice(d3,11));
        reconnectPlayer();
    }

    @Test
    void removeDice(){
        setDices();
        roundTrack.insertDices(listDice, 1);
        try {
            Dice dice = roundTrack.removeDice(1, 1);
            assertEquals(1, roundTrack.getListRounds(1).size());
            assertFalse(roundTrack.getListRounds(1).contains(dice));

            dice = roundTrack.removeDice(1, 0);
            assertEquals(0, roundTrack.getListRounds(1).size());
            assertFalse(roundTrack.getListRounds(1).contains(dice));

        }catch (RemoveDiceException e) {
            e.printStackTrace();
        }
        assertThrows(RemoveDiceException.class,() -> roundTrack.testRemoveDice(11,0,"player 1"));
        assertThrows(RemoveDiceException.class,() -> roundTrack.removeDice(11,0));
    }

    @Test
    void containsColour(){
        setDices();
        roundTrack.insertDices(listDice, 1);

        assertTrue(roundTrack.containsColour(d1.getColour()));
        assertTrue(roundTrack.containsColour(d2.getColour()));
        assertFalse(roundTrack.containsColour(d3.getColour()));
    }
}
