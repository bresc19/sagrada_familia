package it.polimi.ingsw.serverTest.modelTest.boardTest;

import it.polimi.ingsw.server.exception.UseToolException;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.Player;
import it.polimi.ingsw.server.model.board.Schema;
import it.polimi.ingsw.server.model.cards.decks.DeckToolsCard;
import it.polimi.ingsw.server.model.cards.tool.cards.ToolCard;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.server.model.builders.SchemaBuilder.buildSchema;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    private List<Player> players = new ArrayList<>();
    private Player player, player2;
    private Schema defaultSchema;
    private Schema customSchema;

    private void initBoard() {
        player = new Player("player 1");
        player2 = new Player("player 2");
        Player player3 = new Player("player 3");
        players.add(player);
        players.add(player2);
        players.add(player3);
        board = new Board(players);
        defaultSchema = buildSchema(1);
        customSchema = buildSchema(2);
        setTools();
    }

    private void setTools() {
        DeckToolsCard toolsCard = new DeckToolsCard();
        board.setDeckTool(toolsCard.getToolCards());
    }

    @Test
    void playersTest() {
        initBoard();
        assertSame(players, board.getPlayerList());
        assertEquals(3, board.numPlayers());
        assertEquals(0, board.getIndex(player));
    }

    @Test
    void addDefaultSchema() {
        initBoard();
        board.addDefaultSchema(defaultSchema);
        assertEquals(1, board.getDeckSchemas().size());
        assertEquals(defaultSchema, board.getDeckSchemas().get(0));
        board.addDefaultSchema(defaultSchema);
        assertEquals(2, board.getDeckSchemas().size());
        board.addDefaultSchema(defaultSchema);
        assertEquals(3, board.getDeckSchemas().size());
    }

    @Test
    void addCustomSchema() {
        initBoard();
        board.addCustomSchema(customSchema);
        assertEquals(1, board.getDeckSchemas().size());
        assertEquals(customSchema, board.getDeckSchemas().get(0));
        board.addCustomSchema(customSchema);
        assertEquals(2, board.getDeckSchemas().size());
        board.addCustomSchema(customSchema);
        assertEquals(3, board.getDeckSchemas().size());
    }

    @Test
    void getConnected() {
        initBoard();
        assertEquals(3, board.getConnected());
        player.setConnected(false);
        assertEquals(2, board.getConnected());
        player2.setConnected(false);
        assertEquals(1, board.getConnected());
    }

    @Test
    void getToolCard() {
        initBoard();
        List<ToolCard> toolCards = board.getDeckTool();

        try {
            assertEquals(toolCards.get(0), board.getToolCard(toolCards.get(0).getNumber()));
            assertEquals(toolCards.get(1), board.getToolCard(toolCards.get(1).getNumber()));
            assertEquals(toolCards.get(2), board.getToolCard(toolCards.get(2).getNumber()));
        } catch (UseToolException e) {
            e.printStackTrace();
        }
        assertThrows(UseToolException.class, () -> board.getToolCard(13));
    }
}
