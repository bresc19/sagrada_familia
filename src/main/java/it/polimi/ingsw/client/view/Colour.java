//colour assumed by dices or box in a schema

package it.polimi.ingsw.client.view;

public enum Colour {
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m");
    static final String RESET = "\u001B[0m";

    private String escape;

    Colour(String escape) {
        this.escape = escape;
    }

    /**
     * @param c convert string in color
     * @return color
     */
    public static Colour stringToColour(String c)
    {
        if(c.equals("ANSI_GREEN")){ return Colour.ANSI_GREEN; }
        else if(c.equals("ANSI_BLUE")) { return Colour.ANSI_BLUE; }
        else if(c.equals("ANSI_RED")){ return Colour.ANSI_RED; }
        else if(c.equals("ANSI_PURPLE")) { return Colour.ANSI_PURPLE; }
        else if(c.equals("ANSI_YELLOW")) { return Colour.ANSI_YELLOW; }
        return null;
    }

    /**
     * @return color's string
     */
    public String escape() {
        return escape;
    }

    /**
     * @param str is the string that will be coloured
     * @param colour is the color of a new string
     * @return coloured string
     */
    public static String colorString(String str, Colour colour)
    {
        if(colour!=null)
        return colour.escape+str+Colour.RESET;

        return str;
    }

    /**
     * @param colour is the color to convert a string
     * @return color's string
     */
    public String getColour(Colour colour){
        return colour.escape;
    }
}
