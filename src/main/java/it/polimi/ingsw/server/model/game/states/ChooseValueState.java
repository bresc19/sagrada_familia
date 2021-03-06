package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.server.log.Log;
import it.polimi.ingsw.server.exception.ChangeDiceValueException;
import it.polimi.ingsw.server.internal.mesages.Message;

import java.util.logging.Level;

import static it.polimi.ingsw.server.costants.Constants.CHOOSE_VALUE_STATE;
import static it.polimi.ingsw.server.costants.LogConstants.STATE_EXECUTE;
import static it.polimi.ingsw.server.costants.MessageConstants.CHOOSE_VALUE_ACCEPTED;
import static it.polimi.ingsw.server.costants.MessageConstants.CHOOSE_VALUE_ERROR;

public class ChooseValueState extends State {
    /**
     * Sets the value contained in the action list to the pending dice.
     * @param round is the current state
     * @param message contains the current state and the value to be set to the pending dice
     */
    public void execute(Round round, Message message) {
        try {
            round.getPendingDice().setValue(message.getIntegerArgument(0));
            round.getNextActions().remove(0);
            round.notifyChanges(CHOOSE_VALUE_ACCEPTED);
        } catch (ChangeDiceValueException e) {
            Log.getLogger().addLog(e.getMessage(), Level.SEVERE,this.getClass().getName(),STATE_EXECUTE);
            round.notifyChanges(CHOOSE_VALUE_ERROR);
        }
        giveLegalActions(round);
    }

    @Override
    public String toString() { return CHOOSE_VALUE_STATE; }
}
