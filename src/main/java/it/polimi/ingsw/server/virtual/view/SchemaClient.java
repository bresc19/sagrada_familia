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

    /**
     * set name of scheme
     * @param name is the name of scheme
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * set difficult of scheme
     * @param difficult is the number of constraint
     */
    public void setDifficult(int difficult){
        this.difficult = difficult;
    }

    /**
     * get name of schema
     * @return name of scheme
     */
    public String getName() {
        return name;
    }

    /**
     * get difficult of scheme
     * @return  difficult of scheme
     */
    public int getDifficult() {
        return difficult;
    }


    /**
     * set value of dice
     * @param i is index of row
     * @param j is index of column
     * @param number is value of dice
     */
    public void setDiceNumber(int i, int j, int number){
        grid[i][j].setNumber(number);
    }

    /**
     * set colour of dice
     * @param i is index of row
     * @param j is index of column
     * @param colour is colour of dice
     */
    public void setDiceColour(int i, int j, Colour colour){
        grid[i][j].setColour(colour);
    }


    /**
     * set dice constraint
     * @param i is index of row
     * @param j is index of coloumn
     * @param constraint is constraint
     */
    public void setDiceConstraint(int i, int j, String constraint){
        grid[i][j].setConstraint(constraint);
    }

}
