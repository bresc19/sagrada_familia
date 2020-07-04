package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.setUp.TakeDataFile;
import it.polimi.ingsw.client.view.gui.ControllerGUI;

import java.io.IOException;
import java.util.Scanner;

import static it.polimi.ingsw.client.constants.NameConstants.PATH_START_GAME_IMAGE;
import static it.polimi.ingsw.client.constants.printCostants.*;

public class Handler {
    private View v;
    private LoadImage load;
    private TakeDataFile config;
    public Handler() {
        config = new TakeDataFile();
        load = new LoadImage();
        this.setGraphicInterface();
        v.startScene();
        v.setScene(CONNECTION);
        v.setScene(LOGIN);
    }


    /**
     * set graphic interface
     */
    public void setGraphicInterface()
    {
        boolean correct=false;
        String choose;
        while (!correct) {
            Scanner in = new Scanner(System.in);

            try {
                load.displayImage(config.getParameter(PATH_START_GAME_IMAGE));
            } catch (IOException e) {
               Message.println(e.getMessage(),TypeMessage.ERROR_MESSAGE);
            }
            Message.println("\u001B[37m"+CHOOSE_GRAPHIC_INTERFACE,TypeMessage.INFO_MESSAGE);
            Message.println(CLI_CHOOSE,TypeMessage.INFO_MESSAGE);
            Message.println(GUI_CHOOSE,TypeMessage.INFO_MESSAGE);
            choose = in.nextLine();
            if (choose.equals("1"))
            {
                v = new ViewCLI();
                v.setHandler(this);
                correct = true;
            }
            else if(choose.equals("2"))
            {
                String args[]={};
                ViewGUI gui = new ViewGUI();
                v = new ControllerGUI(this);
                gui.setController((ControllerGUI) v);
                gui.main(args);
                correct = true;
            }
            else {
                Message.print(INVALID_PARAMETER,TypeMessage.ERROR_MESSAGE);
                correct = false;
            }
        }

    }

    /**
     * get a view
     * @return view
     */
    public View getView() { return v; }


}

