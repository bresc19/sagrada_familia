package it.polimi.ingsw.server.model.game.states;

import java.util.ArrayList;
import java.util.List;

public class RollDiceState implements State{
    private static String state = "RollDiceState";

    public void execute(Round round, List action){
        round.getPendingDice().rollDice();
    }

    public String nextState(Round round, List action){ return action.get(0) + "State"; }

    private void giveLegalActions(Round round){
        List<String> legalActions = new ArrayList<String>();
        System.out.println(round.getNextActions());
        if(round.getUsingTool() == 0 || round.getNextActions().isEmpty()) {
            round.setUsingTool(0);
            if (!round.isInsertedDice())
                legalActions.add("InsertDice");
            if(!round.isUsedCard())
                legalActions.add("UseToolCard");
            legalActions.add("EndTurn");
        } else{
            legalActions.addAll(round.getNextActions().get(0));
        }
        round.setLegalActions(legalActions);
    }

    @Override
    public String toString (){return state; }
}
