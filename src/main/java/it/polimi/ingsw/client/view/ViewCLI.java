package it.polimi.ingsw.client.view;

import com.google.gson.Gson;
import it.polimi.ingsw.client.clientConnection.Connection;
import it.polimi.ingsw.client.clientConnection.rmi.RmiConnection;
import it.polimi.ingsw.client.clientConnection.socket.SocketConnection;
import it.polimi.ingsw.client.exceptions.WrongInputException;
import it.polimi.ingsw.client.setUp.TakeDataFile;


import java.io.*;
import java.rmi.RemoteException;
import java.util.*;

import static it.polimi.ingsw.client.constants.MessageConstants.CHANGE_VALUE;
import static it.polimi.ingsw.client.constants.MessageConstants.PAINT_ROW;
import static it.polimi.ingsw.client.constants.NameConstants.*;
import static it.polimi.ingsw.client.constants.printCostants.*;
import static it.polimi.ingsw.client.constants.printCostants.CONNECTION_ERROR;
import static it.polimi.ingsw.client.constants.printCostants.NICKNAME_ALREADY_USE;

public class ViewCLI implements View {
    private Scanner input;
    private String username;
    private Connection connection;
    private Handler hand;
    private HashMap<String, Schema> schemas;
    private ArrayList<String> moves;
    private String privateObjective;
    private List<String> publicObjcective;
    private List<Integer> toolCard;
    private List<Dices> diceSpace;
    private boolean correct;
    private boolean endGame;
    private int row;
    private int column;
    private int indexDiceSpace;
    private int nPlayer;
    private int round;
    private boolean gameRunning;
    private Thread schemaThread;
    private Dices pendingDice;
    private int opVal;
    private LoadImage load;
    private int oldRow;
    private int oldColumn;
    private int newRow;
    private int newColumn;
    private int diceValue;
    private List<List<Dices>> roundTrack;
    private int lobbyTimerValue;
    private String pathCustomSchemas;
    private String pathInitDefaultSchema;

    private Thread threadRound;
    private TakeDataFile config;
    private int timerRound;
     ViewCLI() {
         config = new TakeDataFile();
        lobbyTimerValue = Integer.parseInt(config.getParameter(LOBBY_TIMER));
        pathCustomSchemas = config.getParameter(PATH_CUSTOM_SCHEMA);
        pathInitDefaultSchema = config.getParameter(PATH_INIT_DEFAULT_SCHEMA);
        roundTrack = new ArrayList<>();
        load = new LoadImage();
        endGame = false;
        timerRound = 0;
        opVal = 0;
        round = 1;
        username = "";
        gameRunning = true;
        moves = new ArrayList<>();
        input = new Scanner(System.in);
        schemas = new HashMap<>();
        privateObjective = "";
        publicObjcective = new ArrayList<>();
        toolCard = new ArrayList<>();
        diceSpace = new ArrayList<>();
     }


    // set method

    /**
     * set main parameters of game
     * @param scene is parameter to set
     */
    public void setScene(String scene) {
        if (scene.equals(CONNECTION)) this.setConnection();
        else if (scene.equals(LOGIN)) this.setLogin();
    }


    /**
     *  set connection with server
     */
    private void setConnection() {
        correct = false;
        String choose;
        while (!correct) {
            Message.println(CONNECTION_STRING,TypeMessage.INFO_MESSAGE);
            Message.println(CHOOSE1_CONNECTION_STRING,TypeMessage.INFO_MESSAGE);
            Message.println(CHOOSE2_CONNECTION_STRING,TypeMessage.INFO_MESSAGE);
            choose = input.nextLine();
            if (choose.equals("1")) {
                try {
                    connection = new SocketConnection(hand);
                    Thread t = new Thread((SocketConnection) connection);
                    t.start();
                    correct = true;
                } catch (IOException e) {
                    Message.println(CONNECTION_ERROR,TypeMessage.ERROR_MESSAGE);
                }
            } else if (choose.equals("2")) {
                try {
                    connection = new RmiConnection(hand);
                    correct = true;
                } catch (IOException e) {
                    Message.println(CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
                }

            }
        }
        this.startScene();
    }

    /**
     * used to login with server
     */
    private void setLogin() {
        username = "";
        while (username.equals("")) {
            Message.println(INSERT_USERNAME,TypeMessage.INFO_MESSAGE);
            username = input.nextLine();
            if(username.length()>8){
                Message.println(NICKNAME_TOO_LONG,TypeMessage.ERROR_MESSAGE);
                username ="";
            }
        }
        connection.login(username);

    }

    /**
     * used to choose the own schema
     * @param schemas are the patterns card that the user can choose
     */
    public void setSchemas(List<String> schemas) {
        Message.println(INSERT_SCHEMA_NAME,TypeMessage.INFO_MESSAGE);
        HashMap<String, Schema> selSchema = new HashMap<>();
        for (String nameSchema : schemas) {
            selSchema.put(nameSchema, new Schema().InitSchema(pathInitDefaultSchema + nameSchema));
            selSchema.get(nameSchema).splitImageSchema();
        }

        schemaThread = new Thread(() -> {
            try {
                boolean correct;
                String nameSchema="";

                showSchemas(selSchema);
                Message.println(INFO_CUSTOM_SCHEMA, TypeMessage.INFO_MESSAGE);

                correct = false;
                while(!correct) {
                    nameSchema = input.nextLine();
                    if (nameSchema.equals(CHOOSE_LOAD_CUSTOM_SCHEMA) || schemas.contains(nameSchema) || schemaThread.isInterrupted())
                        correct = true;
                    else
                        Message.println(SCHEME_NOT_FOUND, TypeMessage.ERROR_MESSAGE);

                    if (!schemaThread.isInterrupted())
                        if (!nameSchema.equals(CHOOSE_LOAD_CUSTOM_SCHEMA))
                            connection.sendSchema(nameSchema);
                        else
                            correct = loadSchema();
                }
            } catch (Exception e) {
                Message.println(SCHEMA_ALREADY_INSERT,TypeMessage.INFO_MESSAGE);
            }
        });
        schemaThread.start();
        Message.println("\n",TypeMessage.INFO_MESSAGE);
    }

    /**
     * used to set round's diceSpace
     * @param colours are the dice colors of the DiceSpace
     * @param values are the dice value of the DiceSpace
     */
    public void setDiceSpace(List<String> colours, List<Integer> values) {
        diceSpace.clear();
        for (int i = 0; i < colours.size(); i ++) {
            diceSpace.add(new Dices("", values.get(i), Colour.stringToColour(colours.get(i))));
        }
    }

    /**
     * invoked by server to accept InsertDice action
     */
    public void insertDiceAccepted() {
        diceSpace.get(indexDiceSpace).setConstraint(this.schemas.get(username).getGrid()[row][column].getConstraint());
        this.schemas.get(username).getGrid()[row][column] = diceSpace.get(indexDiceSpace);
        schemas.get(username).splitImageSchema();
        schemas.get(username).showImage();
    }

    /**
     * used to remove Dice from DiceSpace
     * @param index of Dice in DiceSpace
     */
    public void pickDiceSpace(int index) {
        diceSpace.remove(index);
    }

    /**
     *  used to notify the user of an insertDiceSpace error
     */
    public void pickDiceSpaceError() {
        Message.println(INDEX_DICE_SPACE_ERROR,TypeMessage.ERROR_MESSAGE);
    }

    /**
     * used to place a Dice in Schema
     * @param nickname is the name of the player to insert the die in the scheme
     * @param row is index of row of scheme
     * @param column is inde of column of schema
     * @param colour is colour of die
     * @param value is value of die
     */
    public void placeDiceSchema(String nickname, int row, int column, String colour, int value) {
        if (!nickname.equals(username)) {
            this.schemas.get(nickname).getGrid()[row][column] =
                    new Dices("", value, Colour.stringToColour(colour));
        }
    }

    /**
     * used to notify the user of an placeDiceSchema error
     */
    public void placeDiceSchemaError() {
        Message.println(ERROR_INSERT_DICE,TypeMessage.ERROR_MESSAGE);
    }

    /**
     * set private objective card
     * @param colour is colour of private objective card
     */
    public void setPrivateCard(String colour) {
        Message.println(PRIVATE_OBJECT_INFO + colour + "\n",TypeMessage.INFO_MESSAGE);
        privateObjective = colour;
    }

    /**
     * set public objective card used in the game
     * @param cards is public objective card
     */
    public void setPublicObjectives(List<String> cards) {
        publicObjcective = cards;
        Message.println("\n",TypeMessage.INFO_MESSAGE);
    }

    /**
     * set tool card used in the game
     * @param cards is tool card
     */
    public void setToolCards(List<Integer> cards) {
        toolCard = cards;
        Message.println("\n",TypeMessage.INFO_MESSAGE);
    }


    /**
     * set handler
     * @param hand is handler
     */
    public void setHandler(Handler hand) {
        this.hand = hand;
    }

    /**
     * create your custom schema
     */
    private void createSchema() {
        int nCostraint = 0;
        Message.println(CREATE_SCHEMA_INFO,TypeMessage.INFO_MESSAGE);
        Schema sc = new Schema();
        Message.println(CHOOSE_GRID_NAME,TypeMessage.INFO_MESSAGE);
        sc.setName(input.nextLine());

        for (int i = 0; i < sc.getGrid().length; i++) {
            for (int j = 0; j < sc.getGrid()[0].length; j++) {
                Message.println(INSERT_CONSTRAINT_INFO + "[" + i + "][" + j + "]",TypeMessage.INFO_MESSAGE);
                if (setConstraint(sc, i, j))
                    nCostraint++;
            }
        }
        sc.setDifficult(nCostraint);
        Message.println(CHOOSE_GRID_INFO,TypeMessage.INFO_MESSAGE);
        sc.splitImageSchema();
        sc.showImage();
        Message.println(MODIFY_GRID_INFO,TypeMessage.INFO_MESSAGE);
        if (input.nextLine().equals("y"))
            modifySchema(sc);
        Message.println(SAVE_SCHEMA_INFO,TypeMessage.INFO_MESSAGE);
        try {
            saveSchema(sc);
        } catch (IOException e) {
            Message.println(ERROR_SAVE_SCHEMA,TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * set constraint of custom scheme
     * @param sc is custom scheme
     * @param i is index of row to set constraint
     * @param j is index of column to set constraint
     * @return true if set constraint otherwise false
     */
    private boolean setConstraint(Schema sc, int i, int j) {
        boolean isConstraint = true;
        String constraint;
        boolean right = false;
        while (!right) {
            right = true;
            Message.print(COLOUR_COSTRAINT + Colour.colorString(GREEN_INFO, Colour.ANSI_GREEN) + Colour.colorString(RED_INFO, Colour.ANSI_RED) + Colour.colorString(BLUE_INFO, Colour.ANSI_BLUE) + Colour.colorString(YELLOW_INFO, Colour.ANSI_YELLOW) + Colour.colorString(PURPLE_INFO, Colour.ANSI_PURPLE)+"\n",TypeMessage.INFO_MESSAGE);

            Message.println(NUMBER_CONSTRAINT,TypeMessage.INFO_MESSAGE);
            Message.println(EMPTY_CONSTRAINT,TypeMessage.INFO_MESSAGE);
            constraint = input.nextLine();
            char word = constraint.charAt(0);
            switch (word) {
                case 'r':
                    sc.getGrid()[i][j].setConstraint(Colour.ANSI_RED.escape());
                    break;
                case 'g':
                    sc.getGrid()[i][j].setConstraint(Colour.ANSI_GREEN.escape());
                    break;
                case 'y':
                    sc.getGrid()[i][j].setConstraint(Colour.ANSI_YELLOW.escape());
                    break;
                case 'b':
                    sc.getGrid()[i][j].setConstraint(Colour.ANSI_BLUE.escape());
                    break;
                case 'p':
                    sc.getGrid()[i][j].setConstraint(Colour.ANSI_PURPLE.escape());
                    break;
                case '1':
                    sc.getGrid()[i][j].setConstraint("1");
                    break;
                case '2':
                    sc.getGrid()[i][j].setConstraint("2");
                    break;
                case '3':
                    sc.getGrid()[i][j].setConstraint("3");
                    break;
                case '4':
                    sc.getGrid()[i][j].setConstraint("4");
                    break;
                case '5':
                    sc.getGrid()[i][j].setConstraint("5");
                    break;
                case '6':
                    sc.getGrid()[i][j].setConstraint("6");
                    break;
                case 'e': {
                    sc.getGrid()[i][j].setConstraint("");
                    isConstraint = false;
                }
                break;
                default:
                    right = false;

            }
            if (!sc.nearConstraint(i, j, sc.getGrid()[i][j].getConstraint())) {
                right = false;
                Message.println(ERROR_CONSTRAINT,TypeMessage.ERROR_MESSAGE);
            }
        }

        return isConstraint;
    }



    /**
     * used to modify custom schema
     * @param s is scheme i want to change
     */
    private void modifySchema(Schema s) {
        int indexRow;
        int indexColumn;
        correct = false;
        while (!correct) {


            try {
                Message.println(INSERT_ROW,TypeMessage.INFO_MESSAGE);
                indexRow = Integer.parseInt(input.nextLine());

                Message.println(INSERT_COLUMN,TypeMessage.INFO_MESSAGE);
                indexColumn = Integer.parseInt(input.nextLine());

                Message.println( ROW_INFO+ indexRow + COLUMN_INFO + indexColumn,TypeMessage.INFO_MESSAGE);

                setConstraint(s, indexRow - 1, indexColumn - 1);

                Message.println(DONE_MODIFY,TypeMessage.CONFIRM_MESSAGE);

                Message.println(YOUR_SCHEME,TypeMessage.INFO_MESSAGE);
                s.splitImageSchema();
                s.showImage();

                Message.println(MODIFY_GRID,TypeMessage.INFO_MESSAGE);

                if (input.nextLine().equals("y"))
                    correct = false;

                    correct = true;

            } catch (NumberFormatException e) {
                correct = false;
            }
        }
    }

    /**
     * used to save custom schema
     * @param s is scheme i want to save
     * @throws IOException is invoked when there are problems with the file
     */
    //
    private void saveSchema(Schema s) throws IOException {
        String schema;
        Gson g = new Gson();
        s.setPaint(null);
        schema = g.toJson(s);
        correct = false;
        String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        jarPath = jarPath.substring(0, jarPath.lastIndexOf("/"));

        while (!correct) {
            String copyPath;
            copyPath = jarPath+config.getParameter(CUSTOM_SCHEME)+s.getName() + ".json";

            System.out.println(copyPath);
            FileWriter fw;
            File file = new File(copyPath);


            if (file.createNewFile()) {

                Message.println(FILE_INFO + copyPath + CREATE,TypeMessage.CONFIRM_MESSAGE);
                fw = new FileWriter(file);
                try(BufferedWriter b = new BufferedWriter(fw)) {
                    b.write(schema);
                    b.flush();
                }finally {
                    fw.close();
                }
                correct = true;
            } else
                Message.println(FILE_INFO + copyPath + NOT_CREATE,TypeMessage.ERROR_MESSAGE);

        }

    }

    /**
     * used to set opponents schemas
     * @param s are the schemas of opponents
     */
    public void setOpponentsSchemas(List<String> s) {
        clearScreen();
        Schema temp = new Schema();

        for (int i = 0; i < s.size(); i = i + 2) {
            if (!s.get(i).equals(this.getName()))
                schemas.put(s.get(i), temp.InitSchema(pathInitDefaultSchema + s.get(i + 1)));
        }
    }

    /**
     * used to set the number of player in the game
     * @param nPlayer is number of players
     */
    public void setNumberPlayer(int nPlayer) {
        this.nPlayer = nPlayer;
    }

    /**
     * invoked when start the rounds
     */
    public void startRound() {
        if (round == 1) {
            threadRound = new Thread(() -> {
                while (gameRunning)
                    chooseMoves();
            });
            threadRound.start();
        }
    }

    /**
     * used to set legal action
     * @param actions are the possible actions
     */
    public void setActions(List<String> actions) {
        String move;
        moves.clear();
        moves.add(SHOW_PRIVATE_OBJECT);
        moves.add(SHOW_PUBLIC_OBJECT);
        moves.add(SHOW_OPPONENTS_SCHEMAS);
        moves.add(SHOW_TOOL_CARD);
        moves.add(SHOW_ROUND_TRACK);
        moves.add(SHOW_SCHEME);
        if (actions == null)
            return;
        moves.add(SHOW_TIMER_ROUND);
        for (String action : actions) {
            move = action;
            switch (action) {
                case INSERT_DICE:
                    move = INSERT_DICE_INFO;
                    break;
                case DRAFT_DICE:
                    move = DRAFT_DICE_INFO;
                    break;
                case  CANCEL_USE_TOOL_CARD:
                    move = CANCEL_USE_TOOL_CARD_INFO;
                    break;
                case  USE_TOOL_CARD:
                    move = USE_TOOL_CARD_INFO;
                    break;
                case END_TURN:
                    move = END_TURN_INFO;
                    break;
                case ROLL_DICE:
                    move = ROLL_DICE_INFO;
                    break;
                case PLACE_DICE:
                    move = PLACE_DICE_INFO ;
                    break;
                case ROLL_DICE_SPACE :
                    move = ROLL_DICE_SPACE_INFO;
                    break;
                case MOVE_DICE:
                    move = MOVE_DICE_INFO;
                    break;
                case FLIP_DICE:
                    move = FLIP_DICE_INFO ;
                    break;
                case SWAP_DICE :
                    move = SWAP_DICE_INFO;
                    break;
                case PLACE_DICE_SPACE:
                    move = PLACE_DICE_SPACE_INFO;
                    break;
                case SWAP_DICE_BAG:
                    move = SWAP_DICE_BAG_INFO;
                    break;
                case CHANGE_VALUE:
                    move = CHANGE_VALUE_INFO;
                    break;
                default:break;
            }
            moves.add(move);
        }
        showMoves();
    }

    // show method


    /**
     * show  the dice space
     */
    private void showDiceSpace() {
        for (Dices d : diceSpace)
            Message.print(d.toString(),TypeMessage.INFO_MESSAGE);

        Message.println("\n",TypeMessage.INFO_MESSAGE);
    }

    /**
     * show the opponents schema
     * @param schema are the opponents schema
     */
    private void showOpponentsSchemas(HashMap<String, Schema> schema) {
        schema.remove(username);
        for (String key : schema.keySet()) {
            schema.get(key).splitImageSchema();
            schema.get(key).getPaint()[0] = key;
        }
        showSchemas(schema);
    }


    /**
     * show all schemas
     * @param schema are the schemas
     */
    private void showSchemas(HashMap<String, Schema> schema) {
        for (int i = 0; i < PAINT_ROW; i++) {
            for (String key : schema.keySet()) {
                Message.print(schema.get(key).getPaint()[i],TypeMessage.INFO_MESSAGE);
                if (i < 3 || i > 6) {
                    for (int j = schema.get(key).getPaint()[i].length(); j < 45; j++)
                        Message.print(" ",TypeMessage.INFO_MESSAGE);
                } else {
                    for (int x = 32; x < 46; x++)
                        Message.print(" ",TypeMessage.INFO_MESSAGE);
                }
            }
            Message.println("",TypeMessage.INFO_MESSAGE);
        }
    }

    /**
     * show private objective
     */
    private void showPrivateObjective() {
        Message.println(PRIVATE_OBJECT_INFO + privateObjective,TypeMessage.INFO_MESSAGE);
    }

    /**
     * show public objective
     */
    private void showPublicObjective() {
        Message.println(PUBLIC_OBJECTIVE_GAME,TypeMessage.INFO_MESSAGE);
        for (String pub : publicObjcective)
        {
            String description="";

            switch(pub)
            {
                case PUBLIC_OBJECTIVE1: description = PUBLIC_OBJCETIVE1_INFO; break;
                case PUBLIC_OBJECTIVE2: description = PUBLIC_OBJECTIVE2_INFO; break;
                case PUBLIC_OBJECTIVE3: description = PUBLIC_OBJECTIVE3_INFO; break;
                case PUBLIC_OBJECTIVE4: description = PUBLIC_OBJECTIVE4_INFO ;break;
                case PUBLIC_OBJECTIVE5: description = PUBLIC_OBJECTIVE5_INFO; break;
                case PUBLIC_OBJECTIVE6: description = PUBLIC_OBJECTIVE6_INFO;break;
                case PUBLIC_OBJECTIVE7: description = PUBLIC_OBJECTIVE7_INFO;break;
                case PUBLIC_OBJECTIVE8: description = PUBLIC_OBJECTIVE8_INFO;break;
                case PUBLIC_OBJECTIVE9: description = PUBLIC_OBJECTIVE9_INFO;break;
                case PUBLIC_OBJECTIVE10: description = PUBLIC_OBJECTIVE10_INFO;break;
                default: break;
            }
            Message.println(description,TypeMessage.INFO_MESSAGE);
        }
    }

    /**
     * show all tool cards
     */
    private void showToolCard() {
        Message.println(TOOL_CARD_GAME,TypeMessage.INFO_MESSAGE);
        for (Integer tool : toolCard)
            try {
                switch (tool) {
                    case 1:
                        Message.println(TOOL_1_INFO, TypeMessage.INFO_MESSAGE);
                        break;
                    case 2:
                        Message.println(TOOL_2_INFO, TypeMessage.INFO_MESSAGE);
                        break;
                    case 3:
                        Message.println(TOOL_3_INFO, TypeMessage.INFO_MESSAGE);
                        break;
                    case 4:
                        Message.println(TOOL_4_INFO, TypeMessage.INFO_MESSAGE);
                        break;
                    case 5:
                        Message.println(TOOL_5_INFO, TypeMessage.INFO_MESSAGE);
                        break;
                    case 6:
                        Message.println(TOOL_6_INFO, TypeMessage.INFO_MESSAGE);
                        break;
                    case 7:
                        Message.println(TOOL_7_INFO, TypeMessage.INFO_MESSAGE);
                        break;
                    case 8:
                        Message.println(TOOL_8_INFO, TypeMessage.INFO_MESSAGE);
                        break;
                    case 9:
                        Message.println(TOOL_9_INFO, TypeMessage.INFO_MESSAGE);
                        break;
                    case 10:
                        Message.println(TOOL_10_INFO, TypeMessage.INFO_MESSAGE);
                        break;
                    case 11:
                        Message.println(TOOL_11_INFO, TypeMessage.INFO_MESSAGE);
                        break;
                    default:
                        Message.println(TOOL_12_INFO, TypeMessage.INFO_MESSAGE);
                }
            } catch (NumberFormatException e) {
                Message.println(ERROR_TOOL_CARD,TypeMessage.ERROR_MESSAGE);
            }
    }

    /**
     * show all possible moves
     */
    private void showMoves() {
        Message.println(CHOOSE_MOVES, TypeMessage.INFO_MESSAGE);
        for (int i = 0; i < moves.size(); i++)
            Message.println((i + 1) + ")" + moves.get(i),TypeMessage.INFO_MESSAGE);
    }

    /**
     * show the round track
     */
    private void showRoundTrack() {
        if (roundTrack.isEmpty())
            Message.println(EMPTY_ROUND_TRACK,TypeMessage.ERROR_MESSAGE);

        for (int i = 0; i < roundTrack.size(); i++) {
            Message.println(ROUND + (i + 1),TypeMessage.INFO_MESSAGE);
            for (Dices dices : roundTrack.get(i))
                Message.print(dices.toString(),TypeMessage.INFO_MESSAGE);
            Message.println("\n",TypeMessage.INFO_MESSAGE);
        }
    }

    // action method


    /**
     * Create the start scene
     */
    public void startScene() {
        int choose;

        Message.println(START_SCENE,TypeMessage.INFO_MESSAGE);
        while (!correct) {
            try {
                correct = true;
                Message.println(START_SCENE_1,TypeMessage.INFO_MESSAGE);
                Message.println(START_SCENE_BUILD_SCHEME,TypeMessage.INFO_MESSAGE);
                Message.println(START_SCENE_STANDARD_SCHEME,TypeMessage.INFO_MESSAGE);
                choose = Integer.parseInt(input.nextLine());
                switch (choose) {
                    case 1:
                        createSchema();
                        break;
                    case 2:
                        break;
                    default:
                        correct = false;
                }
            } catch (NumberFormatException e) {
                Message.println(INVALID_CHOOSE,TypeMessage.ERROR_MESSAGE);
            }
        }

    }

    /**
     * used to load a custom schema
     */
    private boolean loadSchema() {
        String name;
        String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
        pathCustomSchemas = absolutePath+config.getParameter(CUSTOM_SCHEME);
        File f = new File(pathCustomSchemas);
        System.out.println(pathCustomSchemas);
        Schema sc = new Schema();

        Message.println(CHOOSE_LOAD_SCHEME,TypeMessage.INFO_MESSAGE);

        if (Objects.requireNonNull(f.list()).length == 0) {
            Message.println(EMPTY_CUSTOM_SCHEMAS,TypeMessage.ERROR_MESSAGE);
            return false;
        }

        for (String file : Objects.requireNonNull(f.list()))
            Message.println(file.substring(0, file.length() - 5),TypeMessage.INFO_MESSAGE);
        name = input.nextLine();

        if(schemaThread.isInterrupted())
            return true;

        if( Arrays.stream(f.list()).anyMatch(string->string.equals(name+".json"))) {

            try {
                System.out.println(sc.getCustomGson(pathCustomSchemas+name));
                connection.sendCustomSchema(sc.getCustomGson(pathCustomSchemas+name));
                Message.println(LOAD_THIS_SCHEME, TypeMessage.CONFIRM_MESSAGE);
                schemas.put(username, sc.initCustomSchema(pathCustomSchemas+name));
                schemas.get(username).splitImageSchema();
                schemas.get(username).showImage();
                correct=true;
                return true;
            } catch (IOException e) {
                e.getMessage();
            }
        }

            Message.println(SCHEME_NOT_FOUND, TypeMessage.ERROR_MESSAGE);
            Message.println(REINSERT_SCHEME, TypeMessage.INFO_MESSAGE);
                return false;
    }


    /**
     * invoked by server to log the player
     * @param str message
     */

    public void login(String str) {
        switch(str)
        {
            case WELCOME:{
                Message.println(GAME_EARLY_START,TypeMessage.INFO_MESSAGE);
                Message.println(WAIT_PLAYERS,TypeMessage.INFO_MESSAGE);
                break;
            }
            case LOGIN_ERROR_USERNAME: {
                Message.println(NICKNAME_ALREADY_USE, TypeMessage.ERROR_MESSAGE);
                this.setLogin();
                break;
            }
            case LOGIN_ERROR_GAME:
                {
                    Message.println(ERROR_GAME_ALREADY_START, TypeMessage.ERROR_MESSAGE);
                    Message.println(TRY_LATER,TypeMessage.INFO_MESSAGE);
                    break;
                }
            default: break;
        }

    }

    /**
     * notifies the user that another player has joined the game
     * @param name is the name of player who connected
     */
    public void playerConnected(String name) {
        Message.println("",TypeMessage.INFO_MESSAGE);
        Message.println(" \r"+name + PLAYER_ADD_IN_LOBBY,TypeMessage.INFO_MESSAGE);
        nPlayer++;
        Message.println(NUMBER_PLAYER_CONNECT+ nPlayer,TypeMessage.INFO_MESSAGE);
    }

    /**
     *  notifies the user that another player has left the game
     * @param name is the name of player who disconnected
     */
    public void playerDisconnected(String name) {
        Message.println("",TypeMessage.INFO_MESSAGE);
        Message.println("\r"+name + PLAYER_DISCONNECTED,TypeMessage.INFO_MESSAGE);
        nPlayer--;
        Message.println(PLAYER_ALREADY_CONNECT+nPlayer,TypeMessage.INFO_MESSAGE);

    }

    /**
     * display timer connection
     * @param time is the remaining time
     */
    public void timerPing(String time) {
        Colour colour ;
        int percent = (int) (((lobbyTimerValue - Double.parseDouble(time)) / lobbyTimerValue) * 100);
        Message.print("\r" + Colour.colorString("L", Colour.ANSI_GREEN) + Colour.colorString("o", Colour.ANSI_RED) + Colour.colorString("a", Colour.ANSI_BLUE) + Colour.colorString("d", Colour.ANSI_YELLOW) + Colour.colorString("i", Colour.ANSI_PURPLE) + Colour.colorString("n", Colour.ANSI_GREEN) + Colour.colorString("g", Colour.ANSI_BLUE),TypeMessage.INFO_MESSAGE);
        for (int i = 0; i < percent; i++) {
            switch (i / 20) {
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

            Message.print(Colour.colorString("▋", colour),TypeMessage.INFO_MESSAGE);
        }
        for (int i = percent; i < 100; i++) {
            Message.print(" ",TypeMessage.INFO_MESSAGE);
        }
        Message.print(Colour.colorString(percent + "%", Colour.ANSI_YELLOW),TypeMessage.INFO_MESSAGE);
    }

    /**
     * set timer round
     * @param time is time remain to elapse of turn
     */
    public void turnTimerPing(int time) {
        timerRound = time;
    }


    /**
     * show timer round
     */
    public void showTimerRound()
    {
        Message.println(TIMER_ROUND_INFO+timerRound,TypeMessage.INFO_MESSAGE);
    }

    /**
     * invoked when the game starts
     */
    public void createGame() {
        clearScreen();
        Message.println(CREATE_GAME,TypeMessage.CONFIRM_MESSAGE);
    }


    /**
     * invoked when user's schema has been accepted
     * @param name is the scheme's name
     */
    public void chooseSchema(String name) {
        Schema s = new Schema();
        schemas.put(this.getName(), s.InitSchema(pathInitDefaultSchema + name));
        Message.println(APPROVED_SCHEME + name,TypeMessage.CONFIRM_MESSAGE);


        Message.println(WAITING_CHOOSE_PLAYER,TypeMessage.INFO_MESSAGE);
        if (schemaThread.isAlive()) {
            Message.println(TIMER_SCHEME_ELAPSED, TypeMessage.CONFIRM_MESSAGE);
            Message.println(PRESS_ENTER,TypeMessage.CONFIRM_MESSAGE);
            schemaThread.interrupt();
            try {
                schemaThread.join();
            } catch (InterruptedException e) {
                e.getMessage();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * invoked when the turn starts
     * @param name is the name of the player to whom the turn is assigned
     */
    public void startTurn(String name) {
        clearScreen();
        try {
            load.displayImage(config.getParameter(PATH_ROUND_GAME_IMAGE)+ round + ".txt",false);
        } catch (IOException e) {
            e.getMessage();
        }
        Message.println(YOUR_SCHEME2,TypeMessage.INFO_MESSAGE);
        showMyschema();
        showDiceSpace();
        if (!name.equals(username)) {
            Message.println(TURN_START + name,TypeMessage.CONFIRM_MESSAGE);
            setActions(null);
            showMoves();
        } else {
            Message.println(IS_YOUR_TURN,TypeMessage.CONFIRM_MESSAGE);
        }
    }


    /**
     * used to choose the moves
     */
    private void chooseMoves() {
        String move;
        int choose;
        boolean right = false;
        //to be modified later
        while (!right) {
            right = true;
            try {
                move = input.nextLine();

                if(endGame)
                    return;

                if (move.equals(""))
                    throw new WrongInputException();

                choose = Integer.parseInt(move);
                if (choose > moves.size() || choose < 1)
                    throw new WrongInputException();
                switch(moves.get(choose - 1))
                {
                    case INSERT_DICE_INFO: insertDice(); break;
                    case USE_TOOL_CARD_INFO: useToolCard(); break;
                    case MOVE_DICE_INFO:  moveDice(); break;
                    case ROLL_DICE_INFO:rollDice(); break;
                    case END_TURN_INFO : passTurn(); break;
                    case DRAFT_DICE_INFO: draftDice(); break;
                    case PLACE_DICE_INFO: placeDice(); break;
                    case CHANGE_VALUE_INFO: changeValue(); break;
                    case SWAP_DICE_INFO : swapDice(); break;
                    case CANCEL_USE_TOOL_CARD_INFO: cancelToolCard(); break;
                    case FLIP_DICE_INFO : flipDice(); break;
                    case PLACE_DICE_SPACE_INFO : placeDiceSpace(); break;
                    case ROLL_DICE_SPACE_INFO : rollDiceSpace(); break;
                    case SWAP_DICE_BAG_INFO: swapDiceBag(); break;
                    case CHOOSE_VALUE_INFO: chooseValue(); break;
                }

                switch (choose - 1) {
                    case 0: showPrivateObjective();
                        break;
                    case 1: showPublicObjective();
                        break;
                    case 2: showOpponentsSchemas((HashMap<String, Schema>) schemas.clone());
                        break;
                    case 3: showToolCard();
                        break;
                    case 4: showRoundTrack();
                        break;
                    case 5: showMyschema();
                        break;
                    case 6: showTimerRound();
                    break;
                    default:
                        right = false;
                }


            } catch (WrongInputException e) {
                e.getMessage();
                right = false;
            } catch (NumberFormatException e) {
                Message.println(INSERT_NUMBER,TypeMessage.ERROR_MESSAGE);
                right = false;
            }
        }
    }

    /**
     *  roll the dice in dice space
     */
    private void rollDiceSpace() {
        Message.println(ROLL_THIS_DICES,TypeMessage.CONFIRM_MESSAGE);
        connection.rollDiceSpace();
    }


    /**
     * place the dice in dice space
     */
    public void placeDiceSpace() {
        connection.placeDiceSpace();
    }


    /**
     * undo user tool card
     */
    private void cancelToolCard() {
        connection.cancelUseToolCard();
    }


    /**
     * show my scheme
     */
    private void showMyschema() {
        schemas.get(username).splitImageSchema();
        schemas.get(username).showImage();
    }

    /**
     * used to roll dice
     */
    public void rollDice() {
        Message.println(REALLY_ROLL,TypeMessage.INFO_MESSAGE);
        input.nextLine();
        Message.println(IS_RHETORICAL,TypeMessage.CONFIRM_MESSAGE);
        Message.println(ROLL_THIS_DIE,TypeMessage.CONFIRM_MESSAGE);
        connection.rollDice();
    }

    /**
     * used to increment/decrement value of dice
     */
    private void changeValue() {
        Message.println(INCREMENT_DECREMENT_DICE,TypeMessage.INFO_MESSAGE);

        boolean right = false;
        while (!right) {

            String choose = input.nextLine();
            switch (choose)
            {
                case "I": {
                    opVal = 1;
                    connection.changeValue(INCREMENT);
                    right = true;
                    break;
                }
                case "D": {
                    opVal = -1;
                    connection.changeValue(DECREMENT);
                    right = true;
                    break;
                }
                default: {
                    Message.println(INCORRECT_PARAMETER,TypeMessage.ERROR_MESSAGE);
                    right = false;
                }
            }

        }
    }

    /**
     * place dice in schema
     */
    private void placeDice() {
        boolean right = false;
        while (!right) {
            try {
                Message.println(INSERT_ROW_DICE,TypeMessage.INFO_MESSAGE);
                row = Integer.parseInt(input.nextLine()) - 1;
                Message.println(INSERT_COLUMN_DICE,TypeMessage.INFO_MESSAGE);
                column = Integer.parseInt(input.nextLine()) - 1;
                if (row < 0 || row > 3 || column < 0 || column > 4)
                    throw new NumberFormatException();
                right = true;
                connection.sendPlaceDice(row, column);
            } catch (NumberFormatException ex) {
                Message.println(INVALID_INPUT,TypeMessage.ERROR_MESSAGE);
                right = false;
            }
        }
    }

    /**
     *  used to take dice from dice space
     */
    private void draftDice() {
        Message.println(INDEX_DICE_SPACE + diceSpace.size() + ")",TypeMessage.INFO_MESSAGE);
        indexDiceSpace = Integer.parseInt(input.nextLine()) - 1;
        connection.sendDraft(indexDiceSpace);
    }

    /**
     * used to pass the turn
     */
    private void passTurn() {
        connection.sendEndTurn();
    }

    /**
     * used to insert dice
     */
    public void insertDice() {
        correct = false;
        while (!correct) {
            try {
                Message.println(INDEX_DICE_SPACE + diceSpace.size() + ")",TypeMessage.INFO_MESSAGE);
                indexDiceSpace = Integer.parseInt(input.nextLine());
                indexDiceSpace--;
                if (indexDiceSpace < 0 || indexDiceSpace > diceSpace.size())
                    throw new NumberFormatException();

                Message.println(INSERT_ROW2,TypeMessage.INFO_MESSAGE);
                row = Integer.parseInt(input.nextLine());
                row--;
                if (row < 0 || row > 3)
                    throw new NumberFormatException();

                Message.println(INSERT_COLUMN2,TypeMessage.INFO_MESSAGE);
                column = Integer.parseInt(input.nextLine());
                column--;
                if (column < 0 || column > 4)
                    throw new NumberFormatException();

                correct = true;
                connection.insertDice(indexDiceSpace, row, column);

            } catch (NumberFormatException e) {
                Message.println(INVALID_FORMAT, TypeMessage.ERROR_MESSAGE);
            }
        }

    }


    /**
     * used to clear screen
     */
    private void clearScreen() {
        for (int i = 0; i < 10; i++)
            Message.println("",TypeMessage.INFO_MESSAGE);
    }

    /**
     * invoked to use tool card
     */
    private void useToolCard() {
        correct = false;
        Message.println(CHOOSE_TOOL_CARD,TypeMessage.INFO_MESSAGE);
        for (Integer s : toolCard)
            Message.println(s.toString(),TypeMessage.INFO_MESSAGE);
        int tool = Integer.parseInt(input.nextLine());
        connection.useToolCard(tool);
    }


    /**
     * confirms that the use of the toolcard has been accepted
     * @param favor is favor remain
     */
    public void useToolCardAccepted(int favor) {
        Message.println(CARD_USED + favor,TypeMessage.CONFIRM_MESSAGE);
    }

    /**
     * notifies that the specified tool card has been used
     * @param toolCard the tool card that has been used
     */
    public void usedToolCard(int toolCard) { /*not used by cli*/}

    /**
     * notifies that the specified tool card has not been used
     * @param toolCard the tool card that has not been used
     */
    public void notUsedToolCard(int toolCard) { /*not used by cli*/}

    /**
     * rejects the use of the toolcard
     */
    public void useToolCardError() {
        Message.println(FAVOR_ERROR,TypeMessage.ERROR_MESSAGE);
    }

    /**
     * confirms that change value has been accepted
     */
    public void changeValueAccepted() {
        Message.println(opVal+"",TypeMessage.INFO_MESSAGE);
        pendingDice.setNumber(pendingDice.getNumber() + opVal);
        Message.println(CHANGE_VALUE_ACCEPTED + pendingDice,TypeMessage.CONFIRM_MESSAGE);
    }

    /**
     * rejects change value
     */
    public void changeValueError() {
        Message.println(ERROR_CHANGE_VALUE,TypeMessage.ERROR_MESSAGE);
    }

    /**
     * confirms place dice
     */
    public void placeDiceAccepted() {
        Message.println(CORRECT_INSERT_DICE,TypeMessage.INFO_MESSAGE);
        schemas.get(username).getGrid()[row][column].setNumber(pendingDice.getNumber());
        schemas.get(username).getGrid()[row][column].setColour(pendingDice.getColour());
        pendingDice = null;
    }

    /**
     * confirm roll dice
     * @param value is the new value of dice
     */
    public void rollDiceAccepted(int value) {
        pendingDice.setNumber(value);
        Message.println(VALUE_DICE + pendingDice,TypeMessage.INFO_MESSAGE);
    }

    /**
     * pick dice from  round track
     * @param nRound is the index of round track
     * @param nDice  is the index of dice
     */
    public void pickDiceRoundTrack(int nRound, int nDice) {
        pendingDice = roundTrack.get(nRound).get(nDice);
        roundTrack.get(nRound).remove(nDice);
    }

    /**
     * reject pick dice from round track
     */
    public void pickDiceRoundTrackError() {
        Message.println(ERROR_DICE_NOT_FOUND,TypeMessage.ERROR_MESSAGE);
    }

    /**
     * place dices to round track
     * @param nRound is index of round track
     * @param colours are colours of dices
     * @param values are value of dices
     */
    public void placeDiceRoundTrack(int nRound, List<String> colours, List<Integer> values) {
        round++;
        if (nRound > roundTrack.size() - 1)
            roundTrack.add(new ArrayList<>());
        for (int i = 0; i < colours.size(); i ++) {
            roundTrack.get(nRound).add(new Dices("", values.get(i), Colour.stringToColour((colours.get(i)))));
        }
    }

    /**
     * confirm exchange dice
     */
    public void swapDiceAccepted() {
        Message.println(DICE_EXCHANGE_CORRECT,TypeMessage.INFO_MESSAGE);
    }

    /**
     * confirm the invocation of the method cancelUseToolCard
     * @param favor is favors remain
     */
    public void cancelUseToolCardAccepted(int favor) {
        Message.println(UNDO_ACTION + favor,TypeMessage.CONFIRM_MESSAGE);
    }

    /**
     * confirm flip dice
     * @param value is new value of the dice
     */
    public void flipDiceAccepted(int value) {
        Message.println(FLIP_DICE_CORRECT,TypeMessage.CONFIRM_MESSAGE);
        pendingDice.setNumber(value);
    }


    /**
     * confirm place dice in dice space
     */
    public void placeDiceSpaceAccepted() {
        Message.println(CORRECT_INSERT_DICE,TypeMessage.CONFIRM_MESSAGE);
        pendingDice = null;
    }

    /**
     * place dice in dice space
     * @param colour is the colour of dice
     * @param value is the value of dice
     */
    public void placeDiceSpace(String colour, int value) {
        diceSpace.add(new Dices("", value, Colour.stringToColour((colour))));
    }


    /**
     * confirm roll dice space
     */
    public void rollDiceSpaceAccepted() {
        Message.println(ROLL_DICE_CORRECT,TypeMessage.CONFIRM_MESSAGE);
        showDiceSpace();
    }

    /**
     * confirm swap dice bag
     * @param colour is the new colour of dice
     * @param value is the new value of dice
     */
    public void swapDiceBagAccepted(String colour, int value) {
        pendingDice.setColour(Colour.stringToColour(colour));
        pendingDice.setNumber(value);
        Message.println(VALUE_NEW_DICE + pendingDice,TypeMessage.INFO_MESSAGE);
    }

    /**
     * confirm value accepted
     */
    public void chooseValueAccepted() {
        pendingDice.setNumber(diceValue);
        Message.println(VALUE_EXCHANGE_DICE + pendingDice.getNumber(),TypeMessage.INFO_MESSAGE);
    }


    /**
     * reject choose value
     */
    public void chooseValueError() {
        Message.println(ERROR_EXCHANGE_DICE,TypeMessage.ERROR_MESSAGE);
    }

    /**
     * used to take a dice from round track
     */
    private void swapDice() {
        correct = false;
        while (!correct) {
            try {
                Message.println(INDEX_ROUND_TRACK,TypeMessage.INFO_MESSAGE);
                int nRound = Integer.parseInt(input.nextLine()) - 1;
                Message.println(roundTrack.get(nRound)+"",TypeMessage.INFO_MESSAGE);
                Message.println(INDEX_DICE_ROUND_TRACK,TypeMessage.INFO_MESSAGE);
                int index = Integer.parseInt(input.nextLine()) - 1;
                connection.swapDice(nRound, index);
                correct = true;
            } catch (NumberFormatException e) {
                Message.println(INVALID_PARAMETER,TypeMessage.ERROR_MESSAGE);
            } catch (IndexOutOfBoundsException ex) {
                Message.println(INVALID_INDEX,TypeMessage.ERROR_MESSAGE);
            }
        }
    }


    /**
     * used to shift dice
     */
    private void moveDice() {
        correct = false;
        while (!correct) {
            try {
                Message.println(INDEX_ROW_FROM,TypeMessage.INFO_MESSAGE);
                oldRow = Integer.parseInt(input.nextLine()) - 1;
                Message.println(INDEX_COLUMN_FROM,TypeMessage.INFO_MESSAGE);
                oldColumn = Integer.parseInt(input.nextLine()) - 1;
                Message.println(INDEX_ROW_TO,TypeMessage.INFO_MESSAGE);
                newRow = Integer.parseInt(input.nextLine()) - 1;
                Message.println(INDEX_COLUMN_TO,TypeMessage.INFO_MESSAGE);
                newColumn = Integer.parseInt(input.nextLine()) - 1;
                connection.moveDice(oldRow, oldColumn, newRow, newColumn);
                correct = true;
            } catch (NumberFormatException e) {
                Message.println(INVALID_PARAMETER,TypeMessage.ERROR_MESSAGE);
            }
        }
    }


    /**
     * confirm draft dice
     */
    public void draftDiceAccepted() {
        Message.println(ACTION_CORRECT,TypeMessage.CONFIRM_MESSAGE);
        pendingDice = diceSpace.get(indexDiceSpace);
        Message.println(pendingDice.toString(),TypeMessage.INFO_MESSAGE);
    }

    /**
     * confirm move dice
     */
    public void moveDiceAccepted() {
        this.schemas.get(username).getGrid()[newRow][newColumn].setNumber(this.schemas.get(username).getGrid()[oldRow][oldColumn].getNumber());
        this.schemas.get(username).getGrid()[newRow][newColumn].setColour(this.schemas.get(username).getGrid()[oldRow][oldColumn].getColour());
        this.schemas.get(username).getGrid()[oldRow][oldColumn].setNumber(0);
        this.schemas.get(username).getGrid()[oldRow][oldColumn].setColour(null);

        schemas.get(username).splitImageSchema();
        schemas.get(username).showImage();
        Message.println(MOVE_DICE_CORRECT,TypeMessage.CONFIRM_MESSAGE);
    }

    /**
     * reject move dice
     */
    public void moveDiceError() {
        Message.println(REQUIREMENT_NOT_RESPECT,TypeMessage.ERROR_MESSAGE);
    }


    /**
     * pick dice from schema
     * @param nickname is the owner of scheme
     * @param row is index of row
     * @param column is index of column
     */
    public void pickDiceSchema(String nickname, int row, int column) {
        if (!nickname.equals(username))
            this.schemas.get(nickname).getGrid()[row][column] = new Dices("", 0, null);
    }

    /**
     * reject pick dice schema
     */
    public void pickDiceSchemaError() {
        Message.println(ERROR_DICE_NOT_FOUND,TypeMessage.ERROR_MESSAGE);
    }

    /**
     * @return return own name
     */
    public String getName() {
        return this.username;
    }

    /**
     * flip dice
     */
    private void flipDice() {
        Message.println(FLIP_THIS_DICE,TypeMessage.INFO_MESSAGE);
        connection.flipDice();
    }

    /**
     * swap dice bag
     */
    private void swapDiceBag() {
        Message.println(SWAP_THIS_DICE,TypeMessage.INFO_MESSAGE);
        connection.swapDiceBag();
    }


    /**
     * choose value of dice
     */
    private void chooseValue() {
        Message.println(CHOOSE_DICE_VALUE,TypeMessage.INFO_MESSAGE);
        diceValue = Integer.parseInt(input.nextLine());
        connection.chooseValue(diceValue);
    }

    /**
     * confirm custom schema
     * @param name is name of custom schema
     */
    public void schemaCustomAccepted(String name) {
        Schema s = new Schema();
        schemas.put(this.getName(), s.initCustomSchema(pathCustomSchemas + name));
        Message.println(SCHEMA_APPROVED + name,TypeMessage.CONFIRM_MESSAGE);
        Message.println(WAITING_CHOOSE_PLAYER,TypeMessage.INFO_MESSAGE);
    }

    /**
     * set opponents custom schemas
     * @param s are custom schemas
     */
    public void setOpponentsCustomSchemas(List<String> s) {
        Gson g = new Gson();
        clearScreen();
        Schema temp;

        for (int i = 0; i < s.size(); i = i + 2) {
            if (!s.get(i).equals(this.getName())) {
                temp = g.fromJson(s.get(i + 1), Schema.class);
                schemas.put(s.get(i), temp);
            }
        }
    }

    /**
     * set winner player
     * @param nickname is name of the winner
     */
    public void setWinner(String nickname) {
        clearScreen();
        endGame=true;
        gameRunning=false;
        String winner;
        if(nickname.equals(username))
            winner = PATH_WIN_IMAGE;
        else
            winner = PATH_LOSE_IMAGE;

        try {
            load.displayImage(config.getParameter(winner),false);
        } catch (IOException e) {
            e.getMessage();
        }
        Message.println("",TypeMessage.INFO_MESSAGE);

    }

    /**
     * restart the game
     */
    private void restart()
    {

        Message.println(RESTART_GAME, TypeMessage.INFO_MESSAGE);
        Message.println(PRESS_ENTER,TypeMessage.INFO_MESSAGE);
        try {
            if(schemaThread!=null)
                schemaThread.join();

            if(threadRound != null)
                threadRound.join();
        } catch (InterruptedException e) {
            e.getMessage();
            Thread.currentThread().interrupt();
        }
        connection.disconnect();
        endGame = false;
        gameRunning = true;
        toolCard.clear();
        publicObjcective.clear();
        diceSpace.clear();
        roundTrack.clear();
        setScene(CONNECTION);
        setScene(LOGIN);

    }

    /**
     * set ranking
     * @param players are the names of players
     * @param scores are the scores of players
     */
    public void setRankings(List<String> players, List<Integer> scores) {
        Message.println(RANKING,TypeMessage.INFO_MESSAGE);
        Message.println(RANKING_TITLE,TypeMessage.INFO_MESSAGE);
        for(int i = 0; i < players.size(); i++){
            Message.println("┏---┓--------------┓-----------┓",TypeMessage.INFO_MESSAGE);
            Message.print("║ "+(i+1) + " ║  "+ players.get(i),TypeMessage.INFO_MESSAGE);

            for(int j = players.get(i).length();j<12;j++)
            {
                Message.print(" ",TypeMessage.INFO_MESSAGE);
            }
            Message.print("║    ",TypeMessage.INFO_MESSAGE);
            Message.print(scores.get(i)+"",TypeMessage.INFO_MESSAGE);
            for(int j = scores.get(i).toString().length();j<7;j++)
            {
                Message.print(" ",TypeMessage.INFO_MESSAGE);
            }
            Message.println("║",TypeMessage.INFO_MESSAGE);
            Message.println("┗---┛--------------┛-----------┛",TypeMessage.INFO_MESSAGE);
        }
        restart();
    }

    /**
     * @param players are the name of players
     * @param schemasPlayer are the schemas of players
     */
    public void setSchemasOnReconnect(List<String> players, List<String> schemasPlayer) {

        Thread thread = new Thread(() -> {
            while (gameRunning)
                chooseMoves();
        });
        thread.start();

        Gson gson = new Gson();

        for(int i = 0; i<players.size();i++)
        {
            Schema schema = gson.fromJson(schemasPlayer.get(i),Schema.class);
            schemas.put(players.get(i),schema);
        }

        try {
            load.displayImage(config.getParameter(PATH_ROUND_GAME_IMAGE)+round + ".txt",false);
        } catch (IOException e) {
            e.getMessage();
        }
        Message.println(YOUR_SCHEME2,TypeMessage.INFO_MESSAGE);
        showMyschema();
        showDiceSpace();
        setActions(null);
        showMoves();

    }



}




