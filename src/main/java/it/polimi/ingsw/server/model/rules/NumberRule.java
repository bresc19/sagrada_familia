package it.polimi.ingsw.server.model.rules;

import it.polimi.ingsw.server.log.Log;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.Schema;

import java.util.logging.Level;

import static it.polimi.ingsw.server.costants.Constants.NUMBER_RESTRICTION;
import static it.polimi.ingsw.server.costants.LogConstants.RULE_CHECK_RULE;
import static it.polimi.ingsw.server.costants.LogConstants.RULE_ERROR;


public class NumberRule implements InsertionRule {
    /**
     * Checks if the dice's value is compatible with the number restriction of the specified box of the schema.
     * @param x is the row of the schema where the dice will eventually be inserted.
     * @param y is the column of the schema where the dice will eventually be inserted.
     * @param dice is the dice that will eventually be inserted.
     * @param sch is the schema where the dice will eventually be inserted.
     * @return true if the dice's value is compatible with the box's number restriction, false otherwise.
     */
    public boolean checkRule(int x, int y, Dice dice, Schema sch) {
        if (sch.getTable(x, y).getNumber() == 0)
            return true;
        if (sch.getTable(x, y).getNumber() == dice.getValue())
            return true;
        Log.getLogger().addLog(RULE_ERROR, Level.INFO,this.getClass().getName(),RULE_CHECK_RULE);
        return false;
    }

    public String getRestriction() {
        return NUMBER_RESTRICTION;
    }
}
