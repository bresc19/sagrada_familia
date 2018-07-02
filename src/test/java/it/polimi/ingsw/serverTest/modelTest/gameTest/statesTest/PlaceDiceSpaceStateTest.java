package it.polimi.ingsw.serverTest.modelTest.gameTest.statesTest;

import it.polimi.ingsw.server.internalMesages.Message;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.Colour;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.Player;
import it.polimi.ingsw.server.model.game.GameMultiplayer;
import it.polimi.ingsw.server.model.game.states.PlaceDiceSpaceState;
import it.polimi.ingsw.server.model.game.states.Round;
import it.polimi.ingsw.server.serverConnection.Connected;
import it.polimi.ingsw.server.virtualView.VirtualView;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.server.costants.MessageConstants.PLACE_DICE_SPACE;
import static junit.framework.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaceDiceSpaceStateTest {
    private Round round;
    private PlaceDiceSpaceState state;
    private Dice dice;
    private List<Player> players;
    private Board board;

    private void testInit(){
        state = new PlaceDiceSpaceState();
        players = new ArrayList<>();
        players.add(new Player("player 1"));
        players.add(new Player("player 2"));
        players.add(new Player("player 3"));
        VirtualView view = VirtualView.getVirtualView();
        GameMultiplayer game = new GameMultiplayer(players);
        board = game.getBoard();
        board.setObserver(view);
        round = new Round(players.get(0),board,game.getRoundManager(), game);
        round.roundInit();
        dice = new Dice(Colour.ANSI_YELLOW, 6);
        List<List<String>> nextActions = new ArrayList<>();
        nextActions.add(new ArrayList<>());
        round.setNextActions(nextActions);
    }

    private void placeDiceSpace(){
        Message message = new Message(PLACE_DICE_SPACE);
        state.execute(round,message);
    }

    @Test
    void execute(){
        testInit();

        //places the dice in the diceSpace correctly
        round.setPendingDice(dice);
        assertFalse(board.getDiceSpace().getListDice().contains(dice));
        placeDiceSpace();
        assertTrue(board.getDiceSpace().getListDice().contains(dice));
    }
}
