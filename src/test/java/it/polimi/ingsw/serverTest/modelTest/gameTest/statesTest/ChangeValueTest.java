package it.polimi.ingsw.serverTest.modelTest.gameTest.statesTest;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.Player;
import it.polimi.ingsw.server.model.game.states.ChangeValueState;
import it.polimi.ingsw.server.model.game.states.Round;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChangeValueTest {
    List <Player> players = new ArrayList<Player>();
    Board board = new Board(players);
    Round round = new Round(new Player("player 1"),board);
    ChangeValueState state = new ChangeValueState();
    List action = new ArrayList();

    @Test
    public void nameTest(){
        assertTrue(state.toString() == "ChangeValueState");
    }

    @Test
    public void nextStateTest(){
        action.add("PickDice");
        assertTrue(state.nextState(round,action).toString().equals( "PickDiceState"));
    }
}