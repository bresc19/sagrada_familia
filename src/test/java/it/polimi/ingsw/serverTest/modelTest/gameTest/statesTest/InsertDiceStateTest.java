package it.polimi.ingsw.serverTest.modelTest.gameTest.statesTest;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.Colour;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.Player;
import it.polimi.ingsw.server.model.game.GameMultiplayer;
import it.polimi.ingsw.server.model.game.states.InsertDiceState;
import it.polimi.ingsw.server.model.game.states.Round;
import it.polimi.ingsw.server.serverConnection.Connected;
import it.polimi.ingsw.server.virtualView.VirtualView;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.server.costants.MessageConstants.INSERT_DICE;
import static it.polimi.ingsw.server.model.builders.SchemaBuilder.buildSchema;
import static junit.framework.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InsertDiceStateTest {
    private Round round;
    private InsertDiceState state;
    private List action = new ArrayList();
    private Dice dice;
    private Dice dice2;
    private List<Player> players;
    private Board board;

    private void testInit(){
        state = new InsertDiceState();
        players = new ArrayList<>();
        players.add(new Player("player 1"));
        players.add(new Player("player 2"));
        players.add(new Player("player 3"));
        VirtualView view = new VirtualView();
        view.setConnection(new Connected());
        GameMultiplayer game = new GameMultiplayer(players);
        board = game.getBoard();
        board.setObserver(view);
        players.forEach(player -> player.setObserver(view));
        round = new Round(players.get(0),board,game.getRoundManager(), game);
        round.roundInit();
        dice = new Dice(Colour.ANSI_YELLOW, 5);
        dice2 = new Dice(Colour.ANSI_BLUE,3);
    }

    private void setSchemas(){
        try {
            players.get(0).setSchema(buildSchema(1));
            players.get(1).setSchema(buildSchema(2));
            players.get(2).setSchema(buildSchema(8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertDice(String row, String column){
        action.clear();
        action.add(INSERT_DICE);
        action.add("0");
        action.add(row);
        action.add(column);
        state.execute(round,action);
    }

    @Test
    void execute(){
        testInit();
        setSchemas();

        //first correct dice insertion
        board.getDiceSpace().getListDice().add(0,dice);
        insertDice("0","0");
        assertEquals(dice,round.getCurrentPlayer().getSchema().getTable(0,0).getDice());
        assertNotEquals(dice,board.getDiceSpace().getListDice().get(0));

        //incorrect dice insertion
        board.getDiceSpace().getListDice().add(0,dice2);
        insertDice("0","4");
        assertNull(round.getCurrentPlayer().getSchema().getTable(0,4).getDice());
        assertEquals(dice2,board.getDiceSpace().getListDice().get(0));

        //second correct dice insertion
        insertDice("1","1");
        assertEquals(dice2,round.getCurrentPlayer().getSchema().getTable(1,1).getDice());
        assertNotEquals(dice2,board.getDiceSpace().getListDice().get(0));
    }
}