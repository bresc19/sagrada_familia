package it.polimi.ingsw.server.model.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static it.polimi.ingsw.costants.GameConstants.PICK_DICE_ROUND_TRACK;
import static it.polimi.ingsw.costants.GameConstants.PLACE_DICE_ROUND_TRACK;
import static it.polimi.ingsw.server.serverCostants.Costants.TOT_ROUNDS;

public class RoundTrack extends Observable{
    private List<Dice>[] listRounds;

    public RoundTrack() {
        listRounds = new ArrayList [TOT_ROUNDS];
        for(int i = 0; i< TOT_ROUNDS; i++)
            listRounds[i]=new ArrayList<Dice>();
    }

    public List getListRounds(int i) {
        return this.listRounds[i];
    }

    public Dice getDice(int i, int j) { return this.listRounds[i].get(j);}

    public void insertDices(List<Dice> dices, int nRound) {
        List<String> action = new ArrayList<String>();
        this.listRounds[nRound].addAll(dices);
        action.add(PLACE_DICE_ROUND_TRACK);
        action.add(((Integer)nRound).toString());
        for(Dice d: dices){
            action.add(d.getColour().toString());
            action.add(((Integer)d.getValue()).toString());
        }
        setChanged();
        notifyObservers(action);
    }

    public void insertDice(Dice dice, int nRound) {
        List<String> action = new ArrayList<String>();
        this.listRounds[nRound].add(dice);
        action.add(PLACE_DICE_ROUND_TRACK);
        action.add(((Integer)nRound).toString());
        action.add(dice.getColour().toString());
        action.add(((Integer)dice.getValue()).toString());
        setChanged();
        notifyObservers(action);
    }

    public Dice removeDice(int nRound,int nDice) {
        List<String> action = new ArrayList<String>();
        Dice dice;
        dice = this.listRounds[nRound].get(nDice);
        this.listRounds[nRound].remove(nDice);
        action.add(PICK_DICE_ROUND_TRACK);
        action.add(((Integer)nRound).toString());
        action.add(((Integer)nDice).toString());
        setChanged();
        notifyObservers(action);
        return dice;
    }


    @Override
    public String toString() {
        String str = "";
        for(int i = 0; i< TOT_ROUNDS; i++)
        {
            str+="Round "+(i+1)+"\n";
            if(this.listRounds[i].isEmpty()==false)
            str+= this.listRounds[i].toString()+"\n";
            else
            str+="[empty]\n";
        }
        return str;
    }
    public void dump()
    {
        System.out.println(this);
    }
}
