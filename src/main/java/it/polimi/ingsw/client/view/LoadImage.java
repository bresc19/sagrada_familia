package it.polimi.ingsw.client.view;

import java.io.*;

public class LoadImage {

    /**
     * display image on the screen
     * @param file name of file to read
     * @param colouredImage if image is coloured
     * @throws IOException
     */
    public void displayImage(String file,boolean colouredImage) throws IOException
    {
        InputStream is = LoadImage.class.getResourceAsStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        Colour colour=null;

        int i=0;
        try {
            String sc;
            sc = reader.readLine();
            while(sc!=null)
            {
            if(!colouredImage) {
                i++;

                switch (i / 2) {
                    case 0:
                        colour = Colour.ANSI_GREEN;
                        break;
                    case 1:
                        colour = Colour.ANSI_RED;
                        break;
                    case 2:
                        colour = Colour.ANSI_BLUE;
                        break;
                    case 3:
                        colour = Colour.ANSI_YELLOW;
                        break;
                    default:
                        colour = Colour.ANSI_PURPLE;
                }
            }
                System.out.println(Colour.colorString(sc,colour));
                sc = reader.readLine();
            }
        }
        finally {
            reader.close();
        }
    }
}
