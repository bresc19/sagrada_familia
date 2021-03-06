package it.polimi.ingsw.serverTest.modelTest.gameTest.statesTest;

import it.polimi.ingsw.server.internal.mesages.Message;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.Colour;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.Player;
import it.polimi.ingsw.server.model.game.GameMultiPlayer;
import it.polimi.ingsw.server.model.game.states.PlaceDiceState;
import it.polimi.ingsw.server.model.game.states.Round;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.server.costants.MessageConstants.PLACE_DICE;
import static it.polimi.ingsw.server.model.builders.SchemaBuilder.buildSchema;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PlaceDiceStateTest {
    private Round round;
    private PlaceDiceState state;
    private Dice dice;
    private List<Player> players;
    private Board board;

    private void testInit(){
        state = new PlaceDiceState();
        players = new ArrayList<>();
        players.add(new Player("player 1"));
        players.add(new Player("player 2"));
        players.add(new Player("player 3"));
        GameMultiPlayer game = new GameMultiPlayer(players);
        board = game.getBoard();
        round = new Round(players.get(0),board,game.getRoundManager(), game);
        round.roundInit();
        dice = new Dice(Colour.ANSI_YELLOW, 6);
        List<List<String>> nextActions = new ArrayList<>();
        nextActions.add(new ArrayList<>());
        round.setNextActions(nextActions);
    }

    private void setSchemas() {
        players.get(0).setSchema(buildSchema(1));
        players.get(1).setSchema(buildSchema(3));
        players.get(2).setSchema(buildSchema(5));
        players.get(0).getSchema().setPlayers(board.getNicknames());
        players.get(1).getSchema().setPlayers(board.getNicknames());
        players.get(2).getSchema().setPlayers(board.getNicknames());
    }

    private void placeDice(int row, int column){
        Message message = new Message(PLACE_DICE);
        message.addIntegerArgument(row);
        message.addIntegerArgument(column);
        state.execute(round,message);
    }

    @Test
    void execute(){
        testInit();
        setSchemas();
        round.setPendingDice(dice);

        //Correct dice insertion
        placeDice(0,0);
        assertEquals(dice,round.getCurrentPlayer().getSchema().getTable(0,0).getDice());

        //incorrect dice insertion
        placeDice(1,2);
        assertNull(round.getCurrentPlayer().getSchema().getTable(0,2).getDice());
    }

}