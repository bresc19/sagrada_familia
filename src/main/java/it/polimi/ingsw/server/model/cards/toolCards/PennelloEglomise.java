package it.polimi.ingsw.server.model.cards.toolCards;

import it.polimi.ingsw.server.exception.InsertDiceException;
import it.polimi.ingsw.server.exception.RemoveDiceException;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.Player;
import it.polimi.ingsw.server.model.game.states.Round;

public class PennelloEglomise extends ToolCard {
    private String name = "Pennello per Eglomise";
    private String description = "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di colore. \nDecvi rispettare tutte le altre " +
            "restrizioni di piazzamento";
    private int numCard = 2;
    private boolean used= false;


    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }



    public boolean effects(Player p, Round round, int x, int y, int rows,
                        int columns) {


        Dice d = pickDiceFromSchema(x, y, p.getSchema());
        if(d==null) return false;

        if(placeDiceToSchema(rows, columns, d, p.getSchema(), numCard) == true) {
            try {
                p.getSchema().removeDice(x, y);
            } catch (RemoveDiceException e) {
                e.printStackTrace();
            }
            try {
                p.getSchema().insertDice(rows, columns, d,2);
            } catch (InsertDiceException e) {
                e.printStackTrace();
            }
            if(!this.isUsed()){
                p.setFavour(p.getFavour()-2);
                this.setUsed(true);
            } else{
                p.setFavour(p.getFavour()-1);
            }
            return true;
        }
        else return false;


    }

    public Integer getNum() { return numCard; }

    @Override
    public String toString(){
        String src = "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n";
        src= src + "|" +  this.name.toString() + "\n" + "|" + this.description + "\n" + "|" + "points: " + this.numCard + "\n";
        src = src + "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n";
        return src;

    }

    public void dump(){
        System.out.println(this);
    }



}
