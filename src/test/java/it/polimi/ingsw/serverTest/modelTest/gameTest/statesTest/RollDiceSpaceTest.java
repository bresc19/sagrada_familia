package it.polimi.ingsw.serverTest.modelTest.gameTest.statesTest;

import it.polimi.ingsw.server.internal.mesages.Message;
import it.polimi.ingsw.server.model.board.*;
import it.polimi.ingsw.server.model.game.GameMultiPlayer;
import it.polimi.ingsw.server.model.game.states.RollDiceSpaceState;
import it.polimi.ingsw.server.model.game.states.Round;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.server.costants.MessageConstants.ROLL_DICE_SPACE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class RollDiceSpaceTest {
    private Board board;
    private Round round;
    private RollDiceSpaceState state;

    private void testInit(){
        state = new RollDiceSpaceState();
        List<Player> players = new ArrayList<>();
        players.add(new Player("player 1"));
        players.add(new Player("player 2"));
        players.add(new Player("player 3"));
        GameMultiPlayer game = new GameMultiPlayer(players);
        board = game.getBoard();
        round = new Round(players.get(0),board,game.getRoundManager(), game);
        round.roundInit();
        List<List<String>> nextActions = new ArrayList<>();
        nextActions.add(new ArrayList<>());
        round.setNextActions(nextActions);
    }

    private void rollDiceSpace(){
        Message message = new Message(ROLL_DICE_SPACE);
        state.execute(round,message);
    }

    @Test
    void execute(){
        testInit();
        List<Dice> oldDices = board.getDiceSpace().getListDice();

        //rolls all diceSpace's dices correctly
        rollDiceSpace();
        assertEquals(oldDices.size(),board.getDiceSpace().getListDice().size());
        for(int i=0; i<oldDices.size(); i++)
            assertSame(oldDices.get(i),board.getDiceSpace().getListDice().get(i));

    }
}
