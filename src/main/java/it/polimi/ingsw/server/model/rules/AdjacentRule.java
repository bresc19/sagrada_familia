package it.polimi.ingsw.server.model.rules;

import it.polimi.ingsw.server.log.Log;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.Schema;

import java.util.List;
import java.util.logging.Level;

import static it.polimi.ingsw.server.costants.Constants.ADJACENT_RESTRICTION;
import static it.polimi.ingsw.server.costants.LogConstants.RULE_CHECK_RULE;
import static it.polimi.ingsw.server.costants.LogConstants.RULE_ERROR;

public class AdjacentRule implements InsertionRule {
    /**
     * Checks if there is at least one dice in the schema near the specified position or if the schema is empty.
     * @param x is the row of the schema where the dice will eventually be inserted.
     * @param y is the column of the schema where the dice will eventually be inserted.
     * @param dice is the dice that will eventually be inserted.
     * @param sch is the schema where the dice will eventually be inserted.
     * @return true if there is at least one dice near the box of if it is empty, false otherwise.
     */
    public boolean checkRule(int x, int y, Dice dice, Schema sch) {
        List<Dice> nearDices = sch.nearDice(x, y);

        if (sch.isEmpty()) {
            if (x == 0 || y == 0 || x == 3 || y == 4)
                return true;
            Log.getLogger().addLog(RULE_ERROR, Level.INFO,this.getClass().getName(),RULE_CHECK_RULE);
            return false;
        }

        for (Dice d : nearDices)
            if (d != null)
                return true;
        Log.getLogger().addLog(RULE_ERROR,Level.INFO,this.getClass().getName(),RULE_CHECK_RULE);
        return false;
    }

    public String getRestriction() {
        return ADJACENT_RESTRICTION;
    }

}
