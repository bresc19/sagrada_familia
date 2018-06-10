package it.polimi.ingsw.server.model.game.states;

import java.util.ArrayList;
import java.util.List;

public class FlipDiceState implements State{
    private static String state = "FlipDiceState";

    public void execute(Round round, List action){
        round.getPendingDice().flipDice();
        round.getNextActions().remove(0);
        round.notifyChanges("flipDiceAccepted");
        giveLegalActions(round);
    }

    public String nextState(Round round, List action){ return action.get(0) + "State"; }

    private void giveLegalActions(Round round){
        List<String> legalActions = new ArrayList<String>();
        System.out.println(round.getNextActions());
        if(round.getUsingTool() == null || round.getNextActions().isEmpty()) {
            round.setUsingTool(null);
            if (!round.isInsertedDice() || round.hasBonusInsertDice())
                legalActions.add("InsertDice");
            if(!round.isUsedCard())
                legalActions.add("UseToolCard");
            legalActions.add("EndTurn");
        } else{
            legalActions.addAll(round.getNextActions().get(0));
            if(legalActions.contains("InsertDice") && round.isInsertedDice())
                legalActions.remove("InsertDice");
        }
        round.setLegalActions(legalActions);
    }

    @Override
    public String toString (){return state; }
}