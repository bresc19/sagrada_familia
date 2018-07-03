package it.polimi.ingsw.server.model.rules;

import it.polimi.ingsw.server.Log.Log;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.Schema;

import java.util.List;
import java.util.logging.Level;

import static it.polimi.ingsw.server.costants.Constants.DICES_RESTRICTION;

public class DicesRule implements InsertionRule {
    private static String restriction = DICES_RESTRICTION;

    /**
     * Checks that there isn't dices orthogonally adjacent to the specified position whit the same value or colour
     * of the specified dice.
     * @param x is the row of the schema where the dice will eventually be inserted.
     * @param y is the column of the schema where the dice will eventually be inserted.
     * @param dice is the dice that will eventually be inserted.
     * @param sch is the schema where the dice will eventually be inserted.
     * @return true if no dice with the same colour or value of the specified one has been found, false otherwise.
     */
    public boolean checkRule(int x, int y, Dice dice, Schema sch) {
        List<Dice> nearDices = sch.nearDice(x, y);

        for (int i = 1; i < 4; i = i + 2)
            if (nearDices.get(i) != null)
                if (nearDices.get(i).getValue() == dice.getValue() || nearDices.get(i).getColour() == dice.getColour()) {
                    Log.getLogger().addLog("Adjacent rule error", Level.INFO,this.getClass().getName(),"checkRule");
                    return false;
                }
        for (int i = 4; i < 7; i = i + 2)
            if (nearDices.get(i) != null)
                if (nearDices.get(i).getValue() == dice.getValue() || nearDices.get(i).getColour() == dice.getColour()) {
                    Log.getLogger().addLog("Adjacent rule error", Level.INFO,this.getClass().getName(),"checkRule");
                    return false;
                }
        return true;
    }

    public String getRestriction() {
        return restriction;
    }
}
