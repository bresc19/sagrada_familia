package it.polimi.ingsw.serverTest.modelTest.boardTest;

import com.google.gson.Gson;
import it.polimi.ingsw.server.exception.InsertDiceException;
import it.polimi.ingsw.server.exception.RemoveDiceException;
import it.polimi.ingsw.server.model.board.Colour;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.Schema;
import it.polimi.ingsw.server.virtual.view.SchemaClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.server.model.builders.SchemaBuilder.buildSchema;
import static junit.framework.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

class SchemaTest {
    private Schema s;
    private Gson gson = new Gson();

    private void testInit(){
        s = buildSchema(1);
        s.setPlayers(new ArrayList<>());

    }

    @Test
    void DicePlacement() {
        testInit();
        Dice d = new Dice(Colour.ANSI_YELLOW,6);
        s.silentInsertDice(0,0,d);
        assertNotNull(s.getTable(0,0).getDice(),"The box is not empty");
        try {
            s.testRemoveDice(0,0);
            s.removeDice(0,0);
            assertNull(s.getTable(0,0).getDice(),"The box is empty");
            assertNull(s.removeDice(0,0),"The box is empty");
        } catch (RemoveDiceException e) {
            e.getMessage();
        }
    }


    @Test
    void NearDicesAngle() {
        testInit();

        Dice d1 = new Dice(Colour.ANSI_RED,1);
        Dice d2 = new Dice(Colour.ANSI_BLUE,6);
        Dice d3 = new Dice(Colour.ANSI_PURPLE,3);

        s.silentInsertDice(0,1,d1);
        s.silentInsertDice(1,1,d2);
        s.silentInsertDice(1,0,d3);

        assertTrue(s.nearDice(0,0).contains(d1),"The die is near");
        assertTrue(s.nearDice(0,0).contains(d2),"The die is near");
        assertTrue(s.nearDice(0,0).contains(d3),"The die is near");
        try {
            s.testRemoveDice(1,0);
            s.removeDice(1,0);
        } catch (RemoveDiceException e) {
            e.printStackTrace();
        }
        assertFalse("The die is not near",s.nearDice(0,0).contains(d3));

    }

    // the list returned by nearDice must respect the order
    @Test
    void OrderDice() {
        List <Dice> nearDice;
        testInit();

        Dice d1 = new Dice(Colour.ANSI_PURPLE,3);
        Dice d2 = new Dice(Colour.ANSI_BLUE,4);
        Dice d3 = new Dice(Colour.ANSI_RED,5);
        Dice d4 = new Dice(Colour.ANSI_YELLOW,2);
        Dice d5 = new Dice(Colour.ANSI_RED,5);
        Dice d6 = new Dice(Colour.ANSI_RED,5);
        Dice d7 = new Dice(Colour.ANSI_BLUE,2);
        Dice d8 = new Dice(Colour.ANSI_RED,1);

        s.silentInsertDice(0,0,d1);
        s.silentInsertDice(0,1,d2);
        s.silentInsertDice(0,2,d3);
        s.silentInsertDice(1,0,d4);
        s.silentInsertDice(1,2,d5);
        s.silentInsertDice(2,0,d6);
        s.silentInsertDice(2,1,d7);
        s.silentInsertDice(2,2,d8);

        nearDice = s.nearDice(1,1);

        assertEquals(d1,nearDice.get(0));
        assertEquals(d2,nearDice.get(1));
        assertEquals(d3,nearDice.get(2));
        assertEquals(d4,nearDice.get(3));
        assertEquals(d5,nearDice.get(4));
        assertEquals(d6,nearDice.get(5));
        assertEquals(d7,nearDice.get(6));
        assertEquals(d8,nearDice.get(7));
    }

    @Test
    void insertDice(){
        testInit();
        Dice dice = new Dice(Colour.ANSI_YELLOW,1);
        s.insertDice(0,0,dice);

        assertSame(dice,s.getTable(0,0).getDice());
        assertEquals(1,s.getSize());
        assertEquals(1,s.getDices().size());
        assertSame(dice,s.getDices().get(0));
    }

    @Test
    void testInsertDice(){
        testInit();
        Dice dice = new Dice(Colour.ANSI_YELLOW,1);
        try {
            s.testInsertDice(0,0,dice,null);
            assertEquals(0,s.getSize());
        } catch (InsertDiceException e) {
            e.getMessage();
        }
        assertThrows(InsertDiceException.class,() -> s.testInsertDice(0,1,dice,null));
    }

    @Test
    void silentInsertDice(){
        testInit();
        Dice dice = new Dice(Colour.ANSI_YELLOW,1);
        s.silentInsertDice(0,0,dice);

        assertSame(dice,s.getTable(0,0).getDice());
        assertEquals(1,s.getSize());
        assertEquals(1,s.getDices().size());
        assertSame(dice,s.getDices().get(0));
    }

    @Test
    void testRemoveDice(){
        testInit();
        Dice dice = new Dice(Colour.ANSI_YELLOW,1);
        s.insertDice(0,0,dice);
        try {
            s.testRemoveDice(0,0);

            assertSame(dice,s.getTable(0,0).getDice());
            assertEquals(1,s.getSize());
            assertEquals(1,s.getDices().size());
            assertSame(dice,s.getDices().get(0));
        } catch (RemoveDiceException e) {
            e.printStackTrace();
        }
        assertThrows(RemoveDiceException.class,() -> s.testRemoveDice(0,1));
    }

    @Test
    void removeDice(){
        testInit();
        Dice dice = new Dice(Colour.ANSI_YELLOW,1);
        s.insertDice(0,0,dice);
        s.removeDice(0,0);

        assertSame(null,s.getTable(0,0).getDice());
        assertEquals(0,s.getSize());
        assertEquals(0,s.getDices().size());
    }

    @Test
    void silentRemoveDice(){
        testInit();
        Dice dice = new Dice(Colour.ANSI_YELLOW,1);
        s.insertDice(0,0,dice);
        s.silentRemoveDice(0,0);

        assertSame(null,s.getTable(0,0).getDice());
        assertEquals(0,s.getSize());
        assertEquals(0,s.getDices().size());
    }

    @Test
    void getDicesInRow(){
        testInit();
        Dice dice = new Dice(Colour.ANSI_YELLOW,1);
        Dice dice1 = new Dice(Colour.ANSI_BLUE,6);
        Dice dice2 = new Dice(Colour.ANSI_GREEN,4);
        Dice dice3 = new Dice(Colour.ANSI_YELLOW,2);
        s.insertDice(0,0,dice);
        s.insertDice(0,1,dice1);
        s.insertDice(0,2,dice2);
        s.insertDice(1,0,dice3);

        List<Dice> dices = s.getDicesInRow(0);
        for (int i=0; i<dices.size(); i++)
            assertEquals(s.getTable(0,i).getDice(),dices.get(i));
        assertEquals(3,dices.size());

        assertEquals(1,s.getDicesInRow(1).size());
        assertEquals(dice3,s.getDicesInRow(1).get(0));

        assertTrue(s.getDicesInRow(2).isEmpty());
        assertTrue(s.getDicesInRow(3).isEmpty());
    }

    @Test
    void getDicesInColumn(){
        testInit();
        Dice dice = new Dice(Colour.ANSI_YELLOW,1);
        Dice dice1 = new Dice(Colour.ANSI_BLUE,6);
        Dice dice2 = new Dice(Colour.ANSI_GREEN,4);
        Dice dice3 = new Dice(Colour.ANSI_YELLOW,2);
        s.insertDice(0,0,dice);
        s.insertDice(1,0,dice1);
        s.insertDice(2,0,dice2);
        s.insertDice(0,1,dice3);

        List<Dice> dices = s.getDicesInColumn(0);
        for (int i=0; i<dices.size(); i++)
            assertEquals(s.getTable(i,0).getDice(),dices.get(i));
        assertEquals(3,dices.size());

        assertEquals(1,s.getDicesInColumn(1).size());
        assertEquals(dice3,s.getDicesInColumn(1).get(0));

        assertTrue(s.getDicesInColumn(2).isEmpty());
        assertTrue(s.getDicesInColumn(3).isEmpty());
        assertTrue(s.getDicesInColumn(4).isEmpty());
    }

    @Test
    void generateSchema(){
        SchemaClient schema = new SchemaClient();
        schema.setName("player's schema");
        schema.setDiceConstraint(0,0,"1");
        schema.setDifficult(1);

        s = buildSchema(gson.toJson(schema));
        s.setJson(gson.toJson(schema));

        assertEquals("player's schema",s.getName());
        assertEquals(1,schema.getDifficult());
        assertEquals(1,s.getTable(0,0).getNumber());
        assertEquals(gson.toJson(schema),s.getJson());
    }
}
