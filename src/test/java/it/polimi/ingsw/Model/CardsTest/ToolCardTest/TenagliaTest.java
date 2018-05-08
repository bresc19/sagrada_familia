package it.polimi.ingsw.Model.CardsTest.ToolCardTest;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Cards.ToolCards.Tenaglia;
import it.polimi.ingsw.Model.game.states.Round;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TenagliaTest {
    Schema sch;
    private List<Player> players = new ArrayList<Player>();
    private Player player = new Player("player 1");
    private Player player2 = new Player("player 2");
    private Player player3 = new Player("player 3");
    private Board board ;
    private Round round ;
    Tenaglia toolCard = new Tenaglia();
    Dice d1= new Dice(Colour.ANSI_BLUE, 2);
    Dice d2= new Dice(Colour.ANSI_GREEN, 1);

    {
        try {
            sch = new Schema().schemaInit(7);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setup_round(){
        toolCard.dump();

        players.add(player);
        players.add(player2);
        players.add(player3);
        board = new Board(players);
        round = new Round(player,board);


    }

    @Test
    void correct_use(){
        setup_round();
        round.setCurrentPlayer(player);
        player.setTurn(true);
        round.setPlayerIndex(3);
        round.setTurnNumber(2);

        toolCard.effect(player, round);

        assertEquals(2, round.getBoard().getPlayerList().size());
    }


}