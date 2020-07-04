package it.polimi.ingsw.serverTest.modelTest.gameTest.statesTest;

import it.polimi.ingsw.server.exception.InsertDiceException;
import it.polimi.ingsw.server.internal.mesages.Message;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.Colour;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.Player;
import it.polimi.ingsw.server.model.game.GameMultiPlayer;
import it.polimi.ingsw.server.model.game.states.Round;
import it.polimi.ingsw.server.model.game.states.SwapDiceState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.server.costants.MessageConstants.SWAP_DICE;
import static org.junit.jupiter.api.Assertions.assertSame;


class SwapDiceStateTest {
    private Board board;
    private Round round;
    private SwapDiceState state;
    private Dice dice;
    private Dice dice2;

    private void testInit(){
        state = new SwapDiceState();
        List<Player> players = new ArrayList<>();
        players.add(new Player("player 1"));
        players.add(new Player("player 2"));
        players.add(new Player("player 3"));
        GameMultiPlayer game = new GameMultiPlayer(players);
        board = game.getBoard();
        round = new Round(players.get(0),board,game.getRoundManager(), game);
        round.roundInit();
        dice = new Dice(Colour.ANSI_RED, 6);
        dice2 = new Dice(Colour.ANSI_GREEN,5);
        List<List<String>> nextActions = new ArrayList<>();
        nextActions.add(new ArrayList<>());
        round.setNextActions(nextActions);
    }

    private void insertDiceRoundTrack() {
        try {
            board.getRoundTrack().insertDice( dice,0);
        } catch (InsertDiceException e) {
            e.getMessage();
        }
    }

    private void swapDice(int nRound, int dice){
        Message message = new Message(SWAP_DICE);
        message.addIntegerArgument(nRound);
        message.addIntegerArgument(dice);
        state.execute(round,message);
    }

    @Test
    void execute(){
        testInit();
        insertDiceRoundTrack();
        round.setPendingDice(dice2);

        //correct swap dice
        swapDice(0,0);
        assertSame(dice2,board.getRoundTrack().getDice(0,0));
        assertSame(dice,round.getPendingDice());

        //incorrect swap dice for round index
        swapDice(1,0);
        assertSame(dice2,board.getRoundTrack().getDice(0,0));
        assertSame(dice,round.getPendingDice());

        //incorrect swap dice for dice index
        swapDice(0,1);
        assertSame(dice2,board.getRoundTrack().getDice(0,0));
        assertSame(dice,round.getPendingDice());

    }
}
