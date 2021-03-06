package it.polimi.ingsw.serverTest.modelTest.gameTest.statesTest;

import it.polimi.ingsw.server.internal.mesages.Message;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.Colour;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.Player;
import it.polimi.ingsw.server.model.game.GameMultiPlayer;
import it.polimi.ingsw.server.model.game.states.FlipDiceState;
import it.polimi.ingsw.server.model.game.states.Round;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.server.costants.MessageConstants.ROLL_DICE_SPACE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FlipDiceStateTest {
    private Round round;
    private FlipDiceState state;
    private Dice dice;
    private Dice dice2;

    private void testInit(){
        state = new FlipDiceState();
        List<Player> players = new ArrayList<>();
        players.add(new Player("player 1"));
        players.add(new Player("player 2"));
        players.add(new Player("player 3"));
        dice = new Dice(Colour.ANSI_YELLOW, 5);
        dice2 = new Dice(Colour.ANSI_BLUE,3);
        GameMultiPlayer game = new GameMultiPlayer(players);
        Board board = game.getBoard();
        round = new Round(players.get(0),board,game.getRoundManager(), game);
        round.roundInit();
        List<List<String>> nextActions = new ArrayList<>();
        nextActions.add(new ArrayList<>());
        nextActions.add(new ArrayList<>());
        round.setNextActions(nextActions);
    }

    private void flipDice(){
        Message message = new Message(ROLL_DICE_SPACE);
        state.execute(round,message);
    }

    @Test
    void execute(){
        testInit();

        //flips the dice correctly
        round.setPendingDice(dice);
        flipDice();
        assertEquals(2,round.getPendingDice().getValue());
        assertEquals(Colour.ANSI_YELLOW,round.getPendingDice().getColour());

        //flips the dice correctly
        round.setPendingDice(dice2);
        flipDice();
        assertEquals(4,round.getPendingDice().getValue());
        assertEquals(Colour.ANSI_BLUE,round.getPendingDice().getColour());
    }
}
