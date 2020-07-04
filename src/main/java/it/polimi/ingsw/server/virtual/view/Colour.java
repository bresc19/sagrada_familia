package it.polimi.ingsw.server.virtual.view;

public enum Colour {
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m");
    static final String RESET = "\u001B[0m";

    private final String escape;

    Colour(String escape) {
        this.escape = escape;
    }

    public static Colour stringToColour(String c) {
        switch (c) {
            case "ANSI_GREEN":
                return Colour.ANSI_GREEN;
            case "ANSI_BLUE":
                return Colour.ANSI_BLUE;
            case "ANSI_RED":
                return Colour.ANSI_RED;
            case "ANSI_PURPLE":
                return Colour.ANSI_PURPLE;
            case "ANSI_YELLOW":
                return Colour.ANSI_YELLOW;
            default:
                break;
        }
        return null;
    }

    public String escape() {
        return escape;
    }
}
