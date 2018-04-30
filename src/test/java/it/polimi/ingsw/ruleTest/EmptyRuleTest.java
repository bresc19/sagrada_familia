package it.polimi.ingsw.ruleTest;

import com.google.gson.Gson;
import it.polimi.ingsw.Colour;
import it.polimi.ingsw.Dice;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.Schema;
import it.polimi.ingsw.rules.EmptyRule;
import it.polimi.ingsw.rules.InsertionRule;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmptyRuleTest {
    Schema schema = new Schema();
    Player player = new Player("player 1");
    Dice dice_1 = new Dice(Colour.ANSI_GREEN, 4);
    Dice dice_2 = new Dice(Colour.ANSI_YELLOW, 3);
    InsertionRule rule = new EmptyRule();

    @Test
    public void correctInsertion() throws IOException {
        schema = schema.schemaInit(24);
        schema.insertDice(0,0,dice_1);
        player.setSchema(schema);

        assertTrue(rule.checkRule(player,0,1,0,dice_2));
    }

    @Test
    public void wrongInsertion() throws IOException {
        schema = schema.schemaInit(24);
        schema.insertDice(0,0,dice_1);
        player.setSchema(schema);

        assertFalse(rule.checkRule(player,0,0,0,dice_2));
    }
}

