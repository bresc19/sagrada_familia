package it.polimi.ingsw.server.virtual.view;


import static it.polimi.ingsw.server.costants.Constants.PAINT_ROW;

public class SchemaClient {
    private String name;
    private final Dices [][] grid ;
    private int difficult;
    private static final int ROWS = 4;
    private static final int COLUMNS = 5;
    private final String[] paint ;

    public SchemaClient() {
        paint = new String[PAINT_ROW];
        for(int i=0;i<PAINT_ROW;i++)
            paint[i] ="";

        grid = new Dices[ROWS][COLUMNS];
        for(int i=0;i<ROWS;i++)
        {
            for (int j=0;j<COLUMNS;j++)
            {
                grid[i][j]= new Dices();
            }
        }
    }

    public Dices[][] getGrid()
    {
        return this.grid;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDifficult(int difficult){
        this.difficult = difficult;
    }

    public String getName() {
        return name;
    }

    public int getDifficult() {
        return difficult;
    }

    public void setDiceNumber(int i, int j, int number){
        grid[i][j].setNumber(number);
    }

    public void setDiceColour(int i, int j, Colour colour){
        grid[i][j].setColour(colour);
    }

    public void setDiceConstraint(int i, int j, String constraint){
        grid[i][j].setConstraint(constraint);
    }

}
