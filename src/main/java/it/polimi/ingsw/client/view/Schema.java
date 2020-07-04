package it.polimi.ingsw.client.view;

import com.google.gson.Gson;

import java.io.*;

import static it.polimi.ingsw.client.constants.MessageConstants.PAINT_ROW;

public class Schema {
    private String name;
    private Dices grid [][];
    int difficult;
    private static final int ROWS = 4;
    private static final int COLUMNS = 5;
    private String paint [];
    public Schema()
    {
        paint = new String[PAINT_ROW];
        for(int i=0;i<PAINT_ROW;i++)
            paint[i] ="";

        grid = new Dices[ROWS][COLUMNS];
        for(int i=0;i<ROWS;i++)
        {
            for (int j=0;j<COLUMNS;j++)
            {
                grid[i][j]= new Dices("",0,null);
            }
        }
    }

    /**
     * @param paint is the image split in row
     */
    public void setPaint(String[] paint) {
        this.paint = paint;
    }

    /**
     * load a standard schema
     * @param nome is the name of the schema
     * @return load Schema
     */
    public Schema InitSchema(String nome)
    {
        Schema sch;
        Gson g = new Gson();
        String gsonString = null;
        try {
            gsonString = getGson(nome);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sch = g.fromJson(gsonString,Schema.class);
        return sch;
    }

    /**
     * get json of standard schema
     * @param nome is names scheme
     * @return json of scheme
     * @throws IOException
     */
    public String getGson(String nome) throws IOException
    {
        final String filePath = nome + ".json";  //import every schema from
        //json file form /src/main/data/Schema/i.json

        String sc="";
        InputStream is = Schema.class.getResourceAsStream(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            sc = reader.readLine();
        }
        catch(IOException e){
            System.out.println(e);
        }
        finally {
            reader.close();
        }

        return sc;
    }

    /**
     * load a custom schema
     * @param nome is the name of the schema
     * @return load Schema
     */
    public Schema initCustomSchema(String nome)
    {
        Schema sch;
        Gson g = new Gson();
        String gsonString = null;
        try {
            gsonString = getCustomGson(nome);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sch = g.fromJson(gsonString,Schema.class);
        return sch;
    }

    /**
     * get json of custom schema
     * @param nome is names scheme
     * @return json of costum scheme
     * @throws IOException
     */
    public String getCustomGson(String nome) throws  IOException
    {
        final String filePath = nome+".json";
        String gson;
        try (BufferedReader b = new BufferedReader(new FileReader(filePath))) {
            gson  = b.readLine();
        }
        return gson;
    }

    /**
     *
     * @return the image split in row
     */
    public String[] getPaint() {
        return paint;
    }

    /**
     *
     * @return grid scheme
     */
    public Dices[][] getGrid()
    {
        return this.grid;
    }


    /**
     * split image in row
     */
    public void splitImageSchema() {
        paint[1]= this.name;

        paint[2]= "┏-----------------------------┓";
        for(int i=0; i<ROWS;i++)
        {
            paint[3+i]= "║  ";
            for(int j=0;j<COLUMNS;j++)
            {
                paint[3+i]+=" "+grid[i][j].toString()+" ";

            }
            paint[3+i]+="  ║";
        }
        paint[7]="┗-----------------------------┛";
        paint[8]= "Difficoltà:";
        for(int i=0;i<difficult;i++)
            paint[8]+= "*";

    }

    /**
     * display scheme
     */
    public void showImage()
    {
        for(int i=0;i<PAINT_ROW;i++)
        {
            System.out.print(paint[i]+"\n");
        }
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
     * @param nConstraint is the number of constraint
     */
    public void setDifficult(int nConstraint)
    {
        int difficult;

        if(nConstraint<8)
            difficult = 1;
        else if(nConstraint<11)
            difficult = 2;
        else if(nConstraint<12)
            difficult=3;
        else if(nConstraint<13)
            difficult=4;
        else if(nConstraint<14)
            difficult=5;
        else
            difficult=6;

        this.difficult = difficult;
    }

    /**
     *
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
     * check constraint all around  position [rows,columns] in constraint
     * @param rows is index of row to check constraint
     * @param columns is index of column to check constraint
     * @param costraint is the constraint
     * @return true if constraint was respected, false otherwise
     */
    public boolean nearConstraint(int rows, int columns, String costraint)
    {
        if(costraint.equals(""))
            return true;


                     if ((checkNearConstraint(rows +1, columns, costraint) &&
                             checkNearConstraint(rows -1, columns , costraint) &&
                             checkNearConstraint(rows , columns + 1, costraint) &&
                             checkNearConstraint(rows , columns - 1, costraint)))
                         return true;


        return false;

    }

    /**
     * check constraint in specific position [rows,columns] in constraint
     * @param rows is index of row to check constraint
     * @param columns is index of column to check constraint
     * @param constraint is the constraint
     * @return true if constraint was respected, false otherwise
     */
    private boolean checkNearConstraint(int rows,int columns,String constraint)
    {

        try {
            if(grid[rows][columns].getConstraint().equals(constraint))
              return false;
        }catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }
        return true;
    }

}
